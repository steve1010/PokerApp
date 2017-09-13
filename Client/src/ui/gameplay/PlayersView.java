package ui.gameplay;

import java.util.ArrayList;
import java.util.List;

import entities.gameplay.Board;
import entities.gameplay.Card;
import entities.gameplay.LocatedImage;
import entities.gameplay.PlayerHand;
import entities.gameplay.Card.Color;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class PlayersView {

	@FXML
	private Pane playersPane, cards0Pane, cards1Pane, cards2Pane, cards3Pane, cards4Pane, cards5Pane, cards6Pane,
			cards7Pane, cards8Pane, cards9Pane;

	@FXML
	private VBox potVbox, vBox0, vBox1, vBox2, vBox3, vBox4, vBox5, vBox6, vBox7, vBox8, vBox9;

	@FXML
	private Label potValue, bet0Lbl, name0Lbl, bet1Lbl, name1Lbl, bet2Lbl, name2Lbl, bet3Lbl, name3Lbl, bet4Lbl,
			name4Lbl, bet5Lbl, name5Lbl, bet6Lbl, name6Lbl, bet7Lbl, name7Lbl, bet8Lbl, name8Lbl, bet9Lbl, name9Lbl;

	@FXML
	private ImageView card10Imv, card20Imv, card11Imv, card21Imv, card12Imv, card22Imv, card13Imv, card23Imv, card14Imv,
			card24Imv, card15Imv, card25Imv, card16Imv, card26Imv, card17Imv, card27Imv, card18Imv, card28Imv,
			card19Imv, card29Imv, boardCard0Imv, boardCard1Imv, boardCard2Imv, boardCard3Imv, boardCard4Imv;

	@FXML
	private HBox boardHbox, pActionHbox, cards1Hbox, cards2Hbox, cards3Hbox, cards4Hbox, cards5Hbox, cards6Hbox,
			cards7Hbox, cards8Hbox, cards9Hbox;

	public void setPlayerName(int id, String name) {
		System.out.println("Player " + id + " Hand: " + name);
		switch (id) {
		case 0:
			name0Lbl.setText(name);
			break;
		case 1:
			name1Lbl.setText(name);
			break;
		case 2:
			name2Lbl.setText(name);
			break;
		case 3:
			name3Lbl.setText(name);
			break;
		case 4:
			name4Lbl.setText(name);
			break;
		case 5:
			name5Lbl.setText(name);
			break;
		case 6:
			name6Lbl.setText(name);
			break;
		case 7:
			name7Lbl.setText(name);
			break;
		case 8:
			name8Lbl.setText(name);
			break;
		case 9:
			name9Lbl.setText(name);
			break;
		default:
			break;
		}
	}

	public void setCard(int playerID, boolean card1, Card card) {
		if (card1) {
			setCard1(playerID, card);

		} else {
			setCard2(playerID, card);
		}
	}

	private void setCard1(int playerID, Card card) {
		switch (playerID) {
		case 0:
			card10Imv.setImage(cardToImage(card));
			break;
		case 1:
			card11Imv.setImage(cardToImage(card));
			break;
		case 2:
			card12Imv.setImage(cardToImage(card));
			break;
		case 3:
			card13Imv.setImage(cardToImage(card));
			break;
		case 4:
			card14Imv.setImage(cardToImage(card));
			break;
		case 5:
			card15Imv.setImage(cardToImage(card));
			break;
		case 6:
			card16Imv.setImage(cardToImage(card));
			break;
		case 7:
			card17Imv.setImage(cardToImage(card));
			break;
		case 8:
			card18Imv.setImage(cardToImage(card));
			break;
		case 9:
			card19Imv.setImage(cardToImage(card));
			break;
		default:
			break;
		}
	}

	private void setCard2(int playerID, Card card) {
		switch (playerID) {
		case 0:
			card20Imv.setImage(cardToImage(card));
			break;
		case 1:
			card21Imv.setImage(cardToImage(card));
			break;
		case 2:
			card22Imv.setImage(cardToImage(card));
			break;
		case 3:
			card23Imv.setImage(cardToImage(card));
			break;
		case 4:
			card24Imv.setImage(cardToImage(card));
			break;
		case 5:
			card25Imv.setImage(cardToImage(card));
			break;
		case 6:
			card26Imv.setImage(cardToImage(card));
			break;
		case 7:
			card27Imv.setImage(cardToImage(card));
			break;
		case 8:
			card28Imv.setImage(cardToImage(card));
			break;
		case 9:
			card29Imv.setImage(cardToImage(card));
			break;
		default:
			break;
		}
	}

	private LocatedImage cardToImage(Card card) {

		StringBuilder imagePath = new StringBuilder();
		imagePath.append("png\\");

		switch (card.getValue()) {
		case 12:
			imagePath.append("ace");
			break;
		case 11:
			imagePath.append("king");
			break;
		case 10:
			imagePath.append("queen");
			break;
		case 9:
			imagePath.append("jack");
			break;
		case 8:
			imagePath.append("10");
			break;
		case 7:
			imagePath.append(9);
			break;
		case 6:
			imagePath.append(8);
			break;
		case 5:
			imagePath.append(7);
			break;
		case 4:
			imagePath.append(6);
			break;
		case 3:
			imagePath.append(5);
			break;
		case 2:
			imagePath.append(4);
			break;
		case 1:
			imagePath.append(3);
			break;
		case 0:
			imagePath.append(2);
			break;
		}

		imagePath.append("_of_");

		switch (card.getColor()) {
		case HEART:
			imagePath.append("hearts");
			break;
		case DIAMONDS:
			imagePath.append("diamonds");
			break;
		case SPADES:
			imagePath.append("spades");
			break;
		case CLUBS:
			imagePath.append("clubs");
		default:
			break;
		}
		imagePath.append(".png");
		return new LocatedImage(getClass().getResourceAsStream(imagePath.toString()), imagePath.toString());
	}

	public PlayerHand getCards(int playerID) {
		return new PlayerHand(getCard1(playerID), getCard2(playerID));
	}

	public List<PlayerHand> getAllCards() {
		List<PlayerHand> hands = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			hands.add(getCards(i));
			}
		return hands;
	}

	private Card getCard2(int playerID) {
		Image image = null;
		switch (playerID) {
		case 0:
			image = card20Imv.getImage();
			break;
		case 1:
			image = card21Imv.getImage();
			break;
		case 2:
			image = card22Imv.getImage();
			break;
		case 3:
			image = card23Imv.getImage();
			break;
		case 4:
			image = card24Imv.getImage();
			break;
		case 5:
			image = card25Imv.getImage();
			break;
		case 6:
			image = card26Imv.getImage();
			break;
		case 7:
			image = card27Imv.getImage();
			break;
		case 8:
			image = card28Imv.getImage();
			break;
		case 9:
			image = card29Imv.getImage();
			break;
		default:
			break;
		}
		return imageToCard((LocatedImage) image);
	}

	private Card getCard1(int playerID) {
		Image image = null;
		switch (playerID) {
		case 0:
			image = card10Imv.getImage();
			break;
		case 1:
			image = card11Imv.getImage();
			break;
		case 2:
			image = card12Imv.getImage();
			break;
		case 3:
			image = card13Imv.getImage();
			break;
		case 4:
			image = card14Imv.getImage();
			break;
		case 5:
			image = card15Imv.getImage();
			break;
		case 6:
			image = card16Imv.getImage();
			break;
		case 7:
			image = card17Imv.getImage();
			break;
		case 8:
			image = card18Imv.getImage();
			break;
		case 9:
			image = card19Imv.getImage();
			break;
		default:
			break;
		}
		return imageToCard((LocatedImage) image);
	}

	private Card imageToCard(LocatedImage image) {

		String[] valueColor = image.getUrl().substring(4).split("_of_");
		int value = -1;

		switch (valueColor[0]) {
		case "ace":
			value = 12;
			break;
		case "king":
			value = 11;
			break;
		case "queen":
			value = 10;
			break;
		case "jack":
			value = 9;
			break;
		case "10":
			value = 8;
			break;
		case "9":
			value = 7;
			break;
		case "8":
			value = 6;
			break;
		case "7":
			value = 5;
			break;
		case "6":
			value = 4;
			break;
		case "5":
			value = 3;
			break;
		case "4":
			value = 2;
			break;
		case "3":
			value = 1;
			break;
		case "2":
			value = 0;
			break;
		default:
			throw new IllegalStateException("Illegal State(value).");
		}

		Color color = null;

		switch (valueColor[1].replaceAll(".png", "")) {
		case "hearts":
			color = Color.HEART;
			break;
		case "diamonds":
			color = Color.DIAMONDS;
			break;
		case "spades":
			color = Color.SPADES;
			break;
		case "clubs":
			color = Color.CLUBS;
			break;
		default:
			throw new IllegalStateException("Illegal State(color). : ´" + valueColor[1] + "´");
		}
		return new Card(value, color);
	}

	public void setBoardCard(int number, Card card) {
		switch (number) {
		case 0:
			boardCard0Imv.setImage(cardToImage(card));
			break;
		case 1:
			boardCard1Imv.setImage(cardToImage(card));
			break;
		case 2:
			boardCard2Imv.setImage(cardToImage(card));
			break;
		case 3:
			boardCard3Imv.setImage(cardToImage(card));
			break;
		case 4:
			boardCard4Imv.setImage(cardToImage(card));
			break;
		default:
			break;
		}
	}

	public Board getBoard() {
		return new Board(imageToCard((LocatedImage) boardCard0Imv.getImage()),
				imageToCard((LocatedImage) boardCard1Imv.getImage()),
				imageToCard((LocatedImage) boardCard2Imv.getImage()),
				imageToCard((LocatedImage) boardCard3Imv.getImage()),
				imageToCard((LocatedImage) boardCard4Imv.getImage()));
	}

}
