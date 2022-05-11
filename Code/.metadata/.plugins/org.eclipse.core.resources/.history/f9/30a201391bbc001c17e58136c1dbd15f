package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.Card.Rank;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author GladeJoa
 */
public class Controller implements Commons, Initializable {

	// === Game Objects ===
	private Deck deck;
	private Player player, dealer;

	// === View Objects ===
	@FXML
	private Pane root;
	
	@FXML
	private Region background;
	
	@FXML
	private BorderPane paneLeft, paneRight;
	
	@FXML
	private Rectangle rectangleLeftBG, rectangleRightBG;

	@FXML
	private HBox containerDealerCards, containerPlayerCards, containerLeftAndRightPane, containerBtnHitAndStand;
	
	@FXML
	private VBox containerPlayerHands, containerGameButtons, containerInitButtons;

	@FXML
	private Slider betSlider;
	
	@FXML
	private Label labelBetSlider, labelGameMessage, labelMoney, labelDealerScore, labelPlayerScore;
	
	@FXML
	private Button btnPlay, btnReset, btnHit, btnStand, btnDoubleDown;
	
	// === Game Logic ===
	private SimpleBooleanProperty inGameOver, inGame, inAnimation, inTwoCardsHands; 

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// === Create logic variables ===
		inGameOver = new SimpleBooleanProperty(false);
		inGame = new SimpleBooleanProperty(false);
		inAnimation = new SimpleBooleanProperty(false);
		inTwoCardsHands = new SimpleBooleanProperty(false);
		
		// === Create Deck and Players ===
		deck = new Deck();
		player = new Player(System.getProperty("user.name"), 150);
		dealer = new Player("Dealer", Integer.MAX_VALUE);

		// === Bind hands to players ===
		player.setHand(new Hand(containerPlayerCards.getChildren()));
		dealer.setHand(new Hand(containerDealerCards.getChildren()));

		// === Add size to components ===
		root.setPrefSize(APP_WIDTH, APP_HEIGHT);
		background.setPrefSize(APP_WIDTH, APP_HEIGHT);
		rectangleLeftBG.setWidth(APP_WIDTH * 0.7 - 1.5 * APP_SPACING);
		rectangleLeftBG.setHeight(APP_HEIGHT - 2 * APP_SPACING);
		rectangleRightBG.setWidth(APP_WIDTH * 0.3 - 1.5 * APP_SPACING);
		rectangleRightBG.setHeight(APP_HEIGHT - 2 * APP_SPACING);

		// == Set first values ===
		labelMoney.setText("CASH: " + player.money());
		labelGameMessage.setText("PRESS PLAY TO PLAY");
		labelBetSlider.setText("CURRENT BET: " + (int) betSlider.getValue());
		playerUpdateScoreMessage(dealer, labelDealerScore);
		playerUpdateScoreMessage(player, labelPlayerScore);
		betSlider.setMax(player.money());

		// === Bind buttons to game logic ===
		BooleanBinding notInGameInAnimationInGameOver = Bindings.or(Bindings.or(inGame.not(), inAnimation), inGameOver);
		BooleanBinding inGameInGameOver = Bindings.or(inGame, inGameOver);

		btnPlay.disableProperty().bind(inGameInGameOver);
		btnDoubleDown.disableProperty().bind(Bindings.or(inTwoCardsHands.not(), notInGameInAnimationInGameOver));
		btnHit.disableProperty().bind(notInGameInAnimationInGameOver);
		btnStand.disableProperty().bind(notInGameInAnimationInGameOver);
		labelBetSlider.disableProperty().bind(inGameInGameOver);
		betSlider.disableProperty().bind(inGameInGameOver);

		// === Player Listeners ===
		player.hand().valueProperty().addListener((obs, old, newValue) -> {
			// ~ Update player value view
			playerUpdateScoreMessage(player, labelPlayerScore);
		});

		player.hand().sizeProperty().addListener((obs, old, newValue) -> {
			// ~  Update inTwoCardsHand boolean property
			inTwoCardsHands.set(newValue.intValue() == 2 ? true : false);

			// ~ End game if player has black jack
			if (newValue.intValue() == PLAYER_HAND_SIZE_BLACK_JACK && player.hand().value() == PLAYER_HAND_MAX_VALUE) {
				endGame();
			}
		});

		player.currentBetProperty().addListener((obs, old, newValue) -> {
			labelBetSlider.setText("CURRENT BET: " + player.currentBet());
		});

		dealer.hand().valueProperty().addListener((obs, old, newValue) -> {
			// ~ Update dealer value view
			playerUpdateScoreMessage(dealer, labelDealerScore);

			// ~ Check if dealer busted.
			if (newValue.intValue() >= PLAYER_HAND_MAX_VALUE) {
				endGame();
			}
		});

		// === Money Listener ===
		player.moneyProperty().addListener((obs, old, newValue) -> {
			betSlider.setMax(newValue.intValue());
			labelMoney.setText("CASH: " + newValue.intValue());
		});

		// === Slider listener ===
		betSlider.valueProperty().addListener((obs, old, newValue) -> {
			player.setCurrentBet((int) betSlider.getValue());
		});
	}

	/**
	 * Called whenever a new game is started. Reset game and deal two new cards
	 * to both players.
	 */
	private void startNewGame() {
		// === Update Logic ===
		inGame.set(true);
		inAnimation.set(true);
		labelGameMessage.setText("");

		// === Reset Game ===
		deck.reset();
		deck.shuffle();
		dealer.resetHand();
		player.resetHand();

		// === Reveal new cards one at a time ===
		ArrayList<Card> playerDrawn = new ArrayList<Card>();
		ArrayList<Card> dealerDrawn = new ArrayList<Card>();

		SequentialTransition revealOneAtATime = new SequentialTransition();
		for (int i = 0; i < 4; i++) {
			Card drawn = deck.drawCard();

			Player giveCardTo = i % 2 == 0 ? dealer : player;
			String drawSound = i % 2 == 0 ? DEALER_DRAW_SOUND : PLAYER_DRAW_SOUND;
			if (i % 2 == 0)
				dealerDrawn.add(drawn);
			else
				playerDrawn.add(drawn);

			addCardToView(drawn, revealOneAtATime, giveCardTo, drawSound);
		}

		// ~ First dealer card should be hidden
		dealer.hand().hideFirstCard();

		// ~ Callback function will add cards to hands and update game logic
		revealOneAtATime.setOnFinished(event -> {
			player.hand().addAllCards(playerDrawn.toArray(new Card[playerDrawn.size()]));
			dealer.hand().addAllCards(dealerDrawn.toArray(new Card[dealerDrawn.size()]));
			inAnimation.set(false);
		});

		// ~ Start animation
		revealOneAtATime.play();
	}

	/**
	 * Called whenever a end game event is triggered. Reveal dealers hand, check
	 * who won and update players money.
	 */
	private void endGame() {
		// === Update Game Logic ===
		inGame.set(false);

		// === Reveal Dealer Hand ===
		dealer.hand().showFirstCard();
		playerUpdateScoreMessage(dealer, labelDealerScore);

		// === Find Winner ===
		int dealerValue = dealer.hand().valueProperty().get();
		int playerValue = player.hand().valueProperty().get();
		Player winner = null;

		if (dealerValue == PLAYER_HAND_MAX_VALUE || playerValue > PLAYER_HAND_MAX_VALUE || dealerValue == playerValue
				|| (dealerValue < PLAYER_HAND_MAX_VALUE && dealerValue > playerValue)) {
			winner = dealer;
			player.setMoney(player.moneyProperty().get() - player.currentBetProperty().get());
		} else {
			winner = player;
			player.setMoney(player.moneyProperty().get() + player.currentBetProperty().get());
		}

		labelGameMessage.setText(winner.name() + " WON");

		// === Game Over ? ===
		if (player.moneyProperty().get() <= 0) {
			gameOver();
		}
	}

	/**
	 * Called when game over. Displays reset button and show game over message.
	 */
	private void gameOver() {
		inGameOver.set(true);
		labelGameMessage.setText("NO MONEY. NO PLAY. GAME OVER");
		btnPlay.setVisible(false);
		btnReset.setVisible(true);
	}

	/**
	 * Called when reset button is pressed. Reset game stats.
	 */
	private void resetGame() {
		// ~ Reset CASH
		player.setMoney(PLAYER_START_MONEY);

		// ~ Reset Game Message
		labelGameMessage.setText("PRESS PLAY TO PLAY");

		// ~ Update Game Logic
		btnReset.setVisible(false);
		btnPlay.setVisible(true);
		inGameOver.set(false);
	}

	/**
	 * Update given label by given player's score.
	 * 
	 * @param player
	 *            : The player that should be used to update score.
	 * @param scoreLabel
	 *            : The player to reveal score on.
	 */
	private void playerUpdateScoreMessage(Player player, Label scoreLabel) {
		scoreLabel.setText(player.name() + ": " + player.hand().visibleValue());
	}

	/**
	 * Function will add an animation for a drawn card onto a players hand,
	 * using the given draw sound.
	 * 
	 * @param drawn
	 *            : The card to add to view.
	 * @param revealOneAtATime
	 *            : The transition manager used to control transitions.
	 * @param player
	 *            : The player to add card too.
	 * @param drawSound
	 *            : The sound that will be played as the card is drawn.
	 */
	private void addCardToView(Card drawn, SequentialTransition revealOneAtATime, Player player, String drawSound) {
		// ~ Add on sound
		PauseTransition ptSound = new PauseTransition(Duration.millis(100));
		ptSound.setOnFinished(ptSoundEvent -> {
			Sound.playSound(drawSound);
		});

		// ~ Add on short fade in effect
		FadeTransition ft = new FadeTransition(Duration.millis(200), drawn);
		ft.setFromValue(0);
		ft.setToValue(1.0);

		// ~ Add on small break between cards
		PauseTransition pt = new PauseTransition(Duration.millis(300));

		// ~ Add all transitions to the Sequential Transition
		revealOneAtATime.getChildren().addAll(ptSound, ft, pt);

		// ~ Add card to player hand
		player.hand().addCardToView(drawn);
	}

	/**
	 * Function will pass turn to dealer and finish their turn.
	 */
	private void playerStand() {
		ArrayList<Card> dealerDrawn = new ArrayList<Card>();
		SequentialTransition revealOneAtATime = new SequentialTransition();

		// === Update Game Logic ===
		inAnimation.set(true);

		// === Show dealer's first card ===
		PauseTransition showFirstCard = new PauseTransition(Duration.millis(100));
		showFirstCard.setOnFinished((ptEvent -> {
			dealer.hand().showFirstCard();
			playerUpdateScoreMessage(dealer, labelDealerScore);
		}));
		revealOneAtATime.getChildren().addAll(showFirstCard, new PauseTransition(Duration.millis(300)));

		// === Reveal cards one at a time ===
		int cnt = dealer.hand().valueProperty().get();
		int numAces = dealer.hand().aces();

		while (cnt < DEALER_HAND_STOP) {
			Card drawn = deck.drawCard();
			dealerDrawn.add(drawn);

			// ~ Add card to dealer view
			addCardToView(drawn, revealOneAtATime, dealer, DEALER_DRAW_SOUND);

			// ~ Accumulate dealer hand sum and number of aces
			cnt += drawn.value();
			if (drawn.rank() == Rank.ACE)
				numAces++;

			// ~ Busted, but got an ace? Subtract 10 and count ace as 1.
			if (cnt > PLAYER_HAND_MAX_VALUE && numAces > 0) {
				cnt -= 10;
				numAces--;
			}
		}

		// ~ Final sum for dealer hand
		final int sum = cnt;

		// ~ Callback function will add cards to hand and update game logic
		revealOneAtATime.setOnFinished(revealOneAtATimeEvent -> {
			dealer.hand().addAllCards(dealerDrawn.toArray(new Card[dealerDrawn.size()]));
			inAnimation.set(false);

			if (sum < PLAYER_HAND_MAX_VALUE) {
				endGame();
			}
		});

		// ~ Start animation
		revealOneAtATime.play();
	}

	/**
	 * Function will let player draw one card.
	 */
	private void playerHit() {
		// ==== Update Game Logic ===
		inAnimation.set(true);

		// === Reveal card ===
		Card drawn = deck.drawCard();
		SequentialTransition revealOneAtATime = new SequentialTransition();
		addCardToView(drawn, revealOneAtATime, player, PLAYER_DRAW_SOUND);

		// ~ Callback function will add card to hand and update game logic
		revealOneAtATime.setOnFinished(revealOneAtATimeEvent -> {
			player.hand().addAllCards(drawn);
			inAnimation.set(false);

			// ~ Did hand bust? End Game. Did hand reach 21? Dealer's turn.
			if (player.hand().valueProperty().get() > PLAYER_HAND_MAX_VALUE) {
				endGame();
			} else if (player.hand().valueProperty().get() == PLAYER_HAND_MAX_VALUE) {
				playerStand();
			}
		});

		// ~ Start animation
		revealOneAtATime.play();
	}

	/**
	 * Function will let player double down.
	 */
	private void playerDoubleDown() {
		// === Update Game Logic ===
		inAnimation.set(true);

		// === Double Bet if possible ===
		int newBet = Math.min(player.currentBetProperty().get() * 2, player.money());
		player.setCurrentBet(newBet);
		betSlider.setValue(newBet);

		// === Draw one additional card then pass turn to dealer ===
		Card drawn = deck.drawCard();
		SequentialTransition revealOneAtATime = new SequentialTransition();
		addCardToView(drawn, revealOneAtATime, player, PLAYER_DRAW_SOUND);

		// ~ Callback function will add card to hand and give control to dealer
		revealOneAtATime.setOnFinished(revealOneAtATimeEvent -> {
			player.hand().addAllCards(drawn);
			inAnimation.set(false);

			// ~ Did player bust?
			if (player.hand().valueProperty().get() > PLAYER_HAND_MAX_VALUE) {
				endGame();
			} else {
				playerStand();
			}
		});

		// ~ Start animation
		revealOneAtATime.play();
	}

	@FXML
	private void btnDoubleDownEvent(ActionEvent click) {
		playerDoubleDown();
	}

	@FXML
	private void btnHitEvent(ActionEvent click) {
		playerHit();
	}

	@FXML
	private void btnStandEvent(ActionEvent click) {
		playerStand();
	}

	@FXML
	private void btnStartEvent(ActionEvent click) {
		startNewGame();
	}

	@FXML
	private void btnResetEvent(ActionEvent click) {
		resetGame();
	}
}