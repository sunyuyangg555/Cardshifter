package com.cardshifter.client;

import com.cardshifter.api.outgoing.CardInfoMessage;
import com.cardshifter.client.buttons.GenericButton;
import com.cardshifter.client.buttons.SavedDeckButton;
import com.cardshifter.client.views.CardHandDocumentController;
<<<<<<< HEAD
import com.cardshifter.client.views.DeckCardController;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
=======

>>>>>>> 1e0a6e91c772035b48bd854258bda21c75df99a9
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
<<<<<<< HEAD
=======

import javafx.event.EventHandler;
>>>>>>> 1e0a6e91c772035b48bd854258bda21c75df99a9
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import net.zomis.cardshifter.ecs.usage.DeckConfig;

public class DeckBuilderWindow {
	@FXML private FlowPane cardListBox;
	@FXML private VBox activeDeckBox;
	@FXML private VBox deckListBox;
	@FXML private AnchorPane previousPage;
	@FXML private AnchorPane nextPage;
	@FXML private AnchorPane saveDeckButton;
	@FXML private AnchorPane loadDeckButton;
	@FXML private TextField deckNameBox;
	@FXML private AnchorPane exitButton;
	@FXML private Label cardCountLabel;
	
	private GameClientLobby lobby;
	private static final int CARDS_PER_PAGE = 12;
	private int currentPage = 0;
	
	private Map<Integer, CardInfoMessage> cardList = new HashMap<>();
	private List<List<CardInfoMessage>> pageList = new ArrayList<>();
	private DeckConfig activeDeckConfig;
	private String deckToLoad;
	private CardInfoMessage cardBeingDragged;
	
	public void acceptDeckConfig(DeckConfig deckConfig, GameClientLobby lobby) {
		this.lobby = lobby;
<<<<<<< HEAD
		this.activeDeckConfig = deckConfig;
		this.cardList = deckConfig.getCardData();
=======
		
		Map<String, Object> configs = message.getConfigs();
		
		for (Map.Entry<String, Object> entry : configs.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof DeckConfig) {
				DeckConfig deckConfig = (DeckConfig) value;
				this.cardList = deckConfig.getCardData();
			}
		}
>>>>>>> 1e0a6e91c772035b48bd854258bda21c75df99a9
	}
	
	public void configureWindow() {
		this.previousPage.setOnMouseClicked(this::goToPreviousPage);
		this.nextPage.setOnMouseClicked(this::goToNextPage);
<<<<<<< HEAD
		this.exitButton.setOnMouseClicked(this::startGame);
		this.saveDeckButton.setOnMouseClicked(this::saveDeck);
		this.loadDeckButton.setOnMouseClicked(this::loadDeck);
		this.activeDeckBox.setOnDragDropped(e -> this.completeDrag(e, true));
		this.activeDeckBox.setOnDragOver(e -> this.completeDrag(e, false));
=======
		
		this.activeDeckBox.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				System.out.println("helpme");
			}
		});
		
		this.activeDeckBox.setOnDragDropped(e -> this.receiveDrag(e, true));
		//this.activeDeckBox.setOnDragEntered(e -> {this.receiveDrag(e);});
		this.activeDeckBox.setOnDragOver(e -> this.receiveDrag(e, false));
		//this.activeDeckAnchorPane.setOnDragDropped(e -> {this.receiveDrag(e);});
		
>>>>>>> 1e0a6e91c772035b48bd854258bda21c75df99a9
		this.pageList = listSplitter(new ArrayList<>(this.cardList.values()), CARDS_PER_PAGE);
		this.displayCurrentPage();
		this.displaySavedDecks();
	}
	
	//File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
	private void displaySavedDecks() {
		this.deckListBox.getChildren().clear();
		File dir = new File("");
		if (dir.listFiles() != null) {
			for (File file : dir.listFiles()) {
				SavedDeckButton deckButton = new SavedDeckButton(this.deckListBox.getPrefWidth(), 40, file.getName(), this);
				this.deckListBox.getChildren().add(deckButton);
			}
		}
	}
	
	private void displayCurrentPage() {
		this.cardListBox.getChildren().clear();
		for (CardInfoMessage message : this.pageList.get(this.currentPage)) {
			CardHandDocumentController card = new CardHandDocumentController(message, null);
<<<<<<< HEAD
			Pane cardPane = card.getRootPane();			
			cardPane.setOnMouseClicked(e -> {this.addCardToActiveDeck(e, message);});
			cardPane.setOnDragDetected(e -> this.startDrag(e, cardPane, message));
=======
			Pane cardPane = card.getRootPane();
			cardPane.setOnDragDetected(e -> this.reportDrag(e, cardPane, card));
			
			
			cardPane.setOnDragDone(event -> System.out.println("dropped it"));
			
			
			
>>>>>>> 1e0a6e91c772035b48bd854258bda21c75df99a9
			this.cardListBox.getChildren().add(cardPane);
		}
	}
	
<<<<<<< HEAD
=======
	private void reportDrag(MouseEvent event, Pane pane, CardHandDocumentController card) {
		Dragboard db = pane.startDragAndDrop(TransferMode.MOVE);
		ClipboardContent content = new ClipboardContent();
		content.put(DataFormat.RTF, pane);
		content.putString(card.toString());
		db.setContent(content);
		
		System.out.println("drag detected");
		System.out.println(card.toString());
		
		event.consume();
	}
	
	private void receiveDrag(DragEvent event, boolean dropped) {
		System.out.println("drag dropped");
//		this.activeDeckBox.getChildren().add(new Label(event.getDragboard().getString()));
//		event.setDropCompleted(true);
		
		event.acceptTransferModes(TransferMode.MOVE);
		if (dropped) {
			this.activeDeckBox.getChildren().add(new Label(event.getDragboard().getString()));
		}
		event.consume();
	}
	
>>>>>>> 1e0a6e91c772035b48bd854258bda21c75df99a9
	private void goToPreviousPage(MouseEvent event) {
		if (this.currentPage > 0) {
			this.currentPage--;
			this.displayCurrentPage();
		}
	}
	private void goToNextPage(MouseEvent event) {
		if (this.currentPage < this.pageList.size() - 1) {
			this.currentPage++;
			this.displayCurrentPage();
		}
	}
	
	private void addCardToActiveDeck(MouseEvent event, CardInfoMessage message) {
		if (this.activeDeckConfig.getTotal() < this.activeDeckConfig.getMaxSize()) {
			if(this.activeDeckConfig.getChosen().get(message.getId()) == null) {
				this.activeDeckConfig.setChosen(message.getId(), 1);
			} else {
				if (this.activeDeckConfig.getChosen().get(message.getId()) < this.activeDeckConfig.getMaxPerCard()) {
					this.activeDeckConfig.setChosen(message.getId(), this.activeDeckConfig.getChosen().get(message.getId()) + 1);
				}
			}
		}
		this.displayActiveDeck();
	}
	
	private void removeCardFromDeck(MouseEvent event, int cardId) {
		if (this.activeDeckConfig.getChosen().get(cardId) != null) {
			this.activeDeckConfig.removeChosen(cardId);
		}
		this.displayActiveDeck();
	}
	
	private void displayActiveDeck() {
		this.activeDeckBox.getChildren().clear();
		for (Integer cardId : this.activeDeckConfig.getChosen().keySet()) {
			DeckCardController card = new DeckCardController(this.cardList.get(cardId), this.activeDeckConfig.getChosen().get(cardId));
			Pane cardPane = card.getRootPane();
			cardPane.setOnMouseClicked(e -> {this.removeCardFromDeck(e, cardId);});
			this.activeDeckBox.getChildren().add(cardPane);
		}
		this.cardCountLabel.setText(String.format("%d / %d", this.activeDeckConfig.getTotal(), this.activeDeckConfig.getMaxSize()));
	}
	
	private void startGame(MouseEvent event) {
		if (this.activeDeckConfig.getTotal() == this.activeDeckConfig.getMaxSize()) {
			this.lobby.sendDeckAndPlayerConfigToServer(this.activeDeckConfig);
		}
	}
	
	private void saveDeck(MouseEvent event) {
		if(!this.deckNameBox.textProperty().get().isEmpty()) {
			try {
				new ObjectMapper().writeValue(new File(this.deckNameBox.textProperty().get()), this.activeDeckConfig);
			} catch (Exception e) {
				System.out.println("Failed to save deck");
				e.printStackTrace();
			}
		}
		this.displaySavedDecks();
	}
	
	private void loadDeck(MouseEvent event) {
		if (this.deckToLoad != null) {
			try {
				this.activeDeckConfig = new ObjectMapper().readValue(this.deckToLoad, DeckConfig.class);
			} catch (Exception e) {
				System.out.println("Deck failed to load");
			}
		}
		this.displayActiveDeck();
	}
	
	public void clearSavedDeckButtons() {
		for (Object button : this.deckListBox.getChildren()) {
			((GenericButton)button).unHighlightButton();
		}
	}
	
	public void setDeckToLoad(String deckName) {
		this.deckToLoad = deckName;
	}
		
	private static <T> List<List<T>> listSplitter(List<T> originalList, int resultsPerList) {
		if (resultsPerList <= 0) {
			throw new IllegalArgumentException("resultsPerList must be positive");
		}
		List<List<T>> listOfLists = new ArrayList<>();
		List<T> latestList = new ArrayList<>();
		Iterator<T> iterator = originalList.iterator();

	    while (iterator.hasNext()) {
		    T next = iterator.next();
			if (latestList.size() >= resultsPerList) {
				listOfLists.add(latestList);
				latestList = new ArrayList<>();
			} 
			latestList.add(next);
		}

		if (!latestList.isEmpty()) {
			listOfLists.add(latestList);
		}

		return listOfLists;
	}
	
	private void startDrag(MouseEvent event, Pane pane, CardInfoMessage message) {
		this.cardBeingDragged = message;
 		Dragboard db = pane.startDragAndDrop(TransferMode.MOVE);
 		ClipboardContent content = new ClipboardContent();
 		content.putString(message.toString());
 		db.setContent(content);
		event.consume();
 	}
	
	private void completeDrag(DragEvent event, boolean dropped) {
		event.acceptTransferModes(TransferMode.MOVE);
		if (dropped) {
			this.addCardToActiveDeck(null, this.cardBeingDragged);
		}
		event.consume();
 	}

}
