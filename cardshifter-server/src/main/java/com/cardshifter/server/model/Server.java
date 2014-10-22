package com.cardshifter.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.cardshifter.api.both.ChatMessage;
import com.cardshifter.api.both.InviteResponse;
import com.cardshifter.api.both.PlayerConfigMessage;
import com.cardshifter.api.incoming.LoginMessage;
import com.cardshifter.api.incoming.RequestTargetsMessage;
import com.cardshifter.api.incoming.ServerQueryMessage;
import com.cardshifter.api.incoming.StartGameRequest;
import com.cardshifter.api.incoming.UseAbilityMessage;
import com.cardshifter.api.messages.Message;
import com.cardshifter.api.outgoing.ClientDisconnectedMessage;
import com.cardshifter.api.outgoing.ServerErrorMessage;
import com.cardshifter.api.outgoing.UserStatusMessage;
import com.cardshifter.api.outgoing.UserStatusMessage.Status;
import com.google.common.util.concurrent.ThreadFactoryBuilder;


/**
 * Handles different parts of the server operations, such as message handling, chat room, game creation, current games
 * @author Simon Forsberg
 *
 */

public class Server {
	private static final Logger	logger = LogManager.getLogger(Server.class);

	private final AtomicInteger clientId = new AtomicInteger(0);
	private final AtomicInteger roomCounter = new AtomicInteger(0);
	private final AtomicInteger gameId = new AtomicInteger(0);
	
	/**
	 * The IncomingHandler receives messages and passes them to the correct Handler
	 */
	private final IncomingHandler incomingHandler;
	private final CommandHandler commandHandler;
	
	private final Map<Integer, ClientIO> clients = new ConcurrentHashMap<>();
	private final Map<Integer, ChatArea> chats = new ConcurrentHashMap<>();
	private final Map<Integer, ServerGame> games = new ConcurrentHashMap<>();
	private final ServerHandler<GameInvite> invites = new ServerHandler<>();
	private final Map<String, GameFactory> gameFactories = new ConcurrentHashMap<>();

	private final Set<ConnectionHandler> handlers = Collections.synchronizedSet(new HashSet<>());
	private final AtomicReference<ClientIO> playAny = new AtomicReference<>();

	private final ScheduledExecutorService scheduler;
	private final ChatArea mainChat;

	public Server() {
		this.incomingHandler = new IncomingHandler(this);
		this.commandHandler = new CommandHandler(this);
		this.scheduler = Executors.newScheduledThreadPool(2, new ThreadFactoryBuilder().setNameFormat("ai-thread-%d").build());
		mainChat = this.newChatRoom("Main");
		
		Handlers handlers = new Handlers(this);
		
		/**
		 * Add a handler for each type of command, message, and method in Handlers
		 */
		incomingHandler.addHandler("login", LoginMessage.class, handlers::loginMessage);
		incomingHandler.addHandler("chat", ChatMessage.class, handlers::chat);
		incomingHandler.addHandler("startgame", StartGameRequest.class, handlers::play);
		incomingHandler.addHandler("use", UseAbilityMessage.class, handlers::useAbility);
		incomingHandler.addHandler("requestTargets", RequestTargetsMessage.class, handlers::requestTargets);
		incomingHandler.addHandler("inviteResponse", InviteResponse.class, handlers::inviteResponse);
		incomingHandler.addHandler("query", ServerQueryMessage.class, handlers::query);
		incomingHandler.addHandler("playerconfig", PlayerConfigMessage.class, handlers::incomingConfig);
	}
	
	/**
	 * 
	 * @return Right now Server just makes this one chat room when created
	 */
	ChatArea getMainChat() {
		return mainChat;
	}
	
	/**
	 * Creates a ChatArea with an id incremented from roomCounter
	 * 
	 * @param name The name of the chat room to create
	 * @return The room that was created
	 */
	public ChatArea newChatRoom(String name) {
		int id = roomCounter.incrementAndGet();
		ChatArea room = new ChatArea(id, name);
		chats.put(id, room);
		return room;
	}
	
	/**
	 * 
	 * @return A collection of the current clients
	 */
	public Map<Integer, ClientIO> getClients() {
		return Collections.unmodifiableMap(clients);
	}
	
	/**
	 * 
	 * @return Server has just one IncomingHandler
	 */
	public IncomingHandler getIncomingHandler() {
		return incomingHandler;
	}

	/**
	 * Passes the message to incomingHandler which will parse and perform it
	 * 
	 * @param client The client sending the message
	 * @param json The actual contents of the message
	 */
	public void handleMessage(ClientIO client, String json) {
		Objects.requireNonNull(client, "Cannot handle message from a null client");
		logger.info("Handle message " + client + ": " + json);
		Message message;
		try {
			message = incomingHandler.parse(json);
			logger.info("Parsed Message: " + message);
			incomingHandler.perform(message, client);
		} catch (Exception e) {
			logger.error("Unable to parse incoming json: " + json, e);
			client.sendToClient(new ServerErrorMessage(e.getMessage()));
		}
	}

	/**
	 * Puts the client in the clients collection
	 * 
	 * @param client All the information about a client
	 */
	public void newClient(ClientIO client) {
		logger.info("New client: " + client);
		client.setId(clientId.incrementAndGet());
		clients.put(client.getId(), client);
	}
	
	/**
	 * Removes client from the clients collection and broadcasts the event
	 * 
	 * @param client All the information about a client
	 */
	public void onDisconnected(ClientIO client) {
		logger.info("Client disconnected: " + client);
		games.values().stream().filter(game -> game.hasPlayer(client))
			.forEach(game -> game.send(new ClientDisconnectedMessage(client.getName(), game.getPlayers().indexOf(client))));
		clients.remove(client.getId());
		getMainChat().remove(client);
		broadcast(new UserStatusMessage(client.getId(), client.getName(), Status.OFFLINE));
	}

	/**
	 * Sends the message to each client in the clients collection
	 * 
	 * @param data The message to broadcast
	 */
	void broadcast(Message data) {
		clients.values().forEach(cl -> cl.sendToClient(data));
	}

	/**
	 * Puts the game factory into the gameFactories collection
	 * 
	 * @param gameType Name of the game type
	 * @param factory The GameFactory object
	 */
	public void addGameFactory(String gameType, GameFactory factory) {
		this.gameFactories.put(gameType, factory);
	}

	/**
	 * 
	 * @return A collection of the current game factories
	 */
	public Map<String, GameFactory> getGameFactories() {
		return Collections.unmodifiableMap(gameFactories);
	}
	
	/**
	 * Puts the created game into the games collection unless its factory is invalid
	 * 
	 * @param parameter the name of the game factory to use
	 * @return A reference to the game object
	 */
	public ServerGame createGame(String parameter) {
		GameFactory suppl = gameFactories.get(parameter);
		if (suppl == null) {
			throw new IllegalArgumentException("No such game factory: " + parameter);
		}
		ServerGame game = suppl.newGame(this, gameId.incrementAndGet());
		this.games.put(game.getId(), game);
		return game;
	}
	
	/**
	 * 
	 * @return A new hash map that contains the contents of chats
	 */
	public Map<Integer, ChatArea> getChats() {
		return new HashMap<>(chats);
	}
	
	/**
	 * 
	 * @return a new hash map that contains the contents of games
	 */
	public Map<Integer, ServerGame> getGames() {
		return new HashMap<>(games);
	}
	
	/**
	 * 
	 * @return The invites ServerHandler object
	 */
	public ServerHandler<GameInvite> getInvites() {
		return invites;
	}

	/**
	 * Adds the ConnectionHandler to the handlers set
	 * 
	 * @param handler the ConnectionHandler to add
	 */
	public void addConnections(ConnectionHandler handler) {
		handler.start();
		this.handlers.add(handler);
	}

	/**
	 * 
	 * @return A reference to (any?) available ClientIO
	 */
	public AtomicReference<ClientIO> getPlayAny() {
		return playAny;
	}

	/**
	 * 
	 * @return The scheduler object
	 */
	public ScheduledExecutorService getScheduler() {
		return scheduler;
	}
	
	/**
	 * Closes all clients, shuts down all handlers, shuts down the scheduler
	 */
	public void stop() {
		for (ClientIO client : new ArrayList<>(clients.values())) {
			client.close();
		}

		for (ConnectionHandler handler : handlers) {
			try {
				handler.shutdown();
			} catch (Exception e) {
				logger.error("Error shutting down " + handler, e);
			}
		}
		this.scheduler.shutdown();
	}
	
	/**
	 * 
	 * @return The CommandHandler object
	 */
	public CommandHandler getCommandHandler() {
		return commandHandler;
	}
	
}
