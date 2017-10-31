package backend.gameplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import entities.gameplay.Board;
import entities.gameplay.Card;
import entities.gameplay.PlayerHand;
import entities.gameplay.hand.Flush;
import entities.gameplay.hand.FourOfAKind;
import entities.gameplay.hand.FullHouse;
import entities.gameplay.hand.HighCard;
import entities.gameplay.hand.Pair;
import entities.gameplay.hand.PokerHand;
import entities.gameplay.hand.RoyalFlush;
import entities.gameplay.hand.Straight;
import entities.gameplay.hand.StraightFlush;
import entities.gameplay.hand.ThreeOfAKind;
import entities.gameplay.hand.TwoPair;

public class Evaluator {

	public String[] evaluate(Board board, List<PlayerHand> hands) {
		String[] handNames = new String[hands.size()];
		for (int i = 0; i < handNames.length; i++) {
			handNames[i] = evaluateHand(hands.get(i), board).toString();
		}
		return handNames;
	}

	// private int evaluateAgainst(PlayerHand hand1, PlayerHand hand2, Board
	// board) {
	//
	// PokerHand pokerHand1 = evaluateHand(hand1, board);
	// PokerHand pokerHand2 = evaluateHand(hand2, board);
	// if (!(pokerHand1.getVal() == pokerHand2.getVal())) {
	// return pokerHand1.getVal() < pokerHand2.getVal() ? 1 : 0;
	// } else {
	// return -1;
	// }
	// }

	private PokerHand evaluateHand(PlayerHand hand, Board board) {

		Card pCard1 = hand.getCard1();
		Card pCard2 = hand.getCard2();

		Card bCard0 = board.getCard0();
		Card bCard1 = board.getCard1();
		Card bCard2 = board.getCard2();
		Card bCard3 = board.getCard3();
		Card bCard4 = board.getCard4();

		Card[] cards = { pCard1, pCard2, bCard0, bCard1, bCard2, bCard3, bCard4 };

		// check flush
		Flush flush = checkFlush(cards);
		if (Objects.nonNull(flush)) {
			StraightFlush str8Flush = checkStraightFlush(cards);
			if (Objects.nonNull(str8Flush)) {
				RoyalFlush royalFlush = checkRoyalFlush(cards);
				if (Objects.nonNull(royalFlush)) {
					return royalFlush;
				}
				return str8Flush;
			}
		}

		// check 4 of a kind
		FourOfAKind fourOfAKind = checkFourOfAKind(cards);
		if (Objects.nonNull(fourOfAKind)) {
			return fourOfAKind;
		}

		// check full house
		FullHouse fullHouse = checkFullHouse(cards);
		if (Objects.nonNull(fullHouse)) {
			return fullHouse;
		}

		// if none of the above, return flush if present
		if (Objects.nonNull(flush)) {
			return flush;
		}

		// check straight
		Straight str8 = checkStraight(cards);
		if (Objects.nonNull(str8)) {
			return str8;
		}

		// check 3 of a kind
		ThreeOfAKind threeOfAKind = checkThreeOfAKind(cards);
		if (Objects.nonNull(threeOfAKind)) {
			return threeOfAKind;
		}

		// check pair & 2 pair
		Pair pair = checkPair(cards);
		if (Objects.nonNull(pair)) {
			List<Card> newCards = Arrays.asList(cards).stream().filter(c -> c.getValue() != pair.getValue())
					.collect(Collectors.toList());
			TwoPair twoPair = checkTwoPair(newCards, pair);
			if (Objects.nonNull(twoPair)) {
				return twoPair;
			}
			return pair;
		}
		return checkHighCard(cards);
	}

	private Flush checkFlush(Card[] cards) {

		ArrayList<Card> cList = new ArrayList<>(), dList = new ArrayList<>(), hList = new ArrayList<>(),
				sList = new ArrayList<>();
		for (Card card : cards) {
			switch (card.getColor()) {
			case CLUBS:
				cList.add(card);
				break;
			case DIAMONDS:
				dList.add(card);
				break;
			case HEART:
				hList.add(card);
				break;
			case SPADES:
				sList.add(card);
				break;
			}
		}
		if (cList.size() > 4) {
			return new Flush(get5Highest(cList));
		}
		if (dList.size() > 4) {
			return new Flush(get5Highest(dList));
		}
		if (hList.size() > 4) {
			return new Flush(get5Highest(hList));
		}
		if (sList.size() > 4) {
			return new Flush(get5Highest(sList));
		}
		return null;
	}

	private StraightFlush checkStraightFlush(Card[] cards) {
		Straight str8 = checkStraight(cards);
		if (Objects.nonNull(str8)) {
			Flush flush = checkFlush(str8.getCards());
			if (Objects.nonNull(flush)) {
				if (flush.getFst() == 12) {
					return new StraightFlush(str8, flush);
				}
			}
		}
		return null;
	}

	private RoyalFlush checkRoyalFlush(Card[] cards) {
		StraightFlush str8Flush = checkStraightFlush(cards);
		if (str8Flush.getStraight().getTop() == 12) {
			return new RoyalFlush(str8Flush.getStraight().getCards()[0].getColor());
		}
		return null;
	}

	private FourOfAKind checkFourOfAKind(Card[] cards) {
		List<Card> cList = new ArrayList<>();
		for (int i = 1; i < cards.length; i++) {
			cList.clear();
			cList.add(cards[i]);
			for (int j = i + 1; j < cards.length; j++) {
				if (cards[j].getValue() == cards[i].getValue()) {
					cList.add(cards[j]);
					if (cList.size() == 4) {
						List<Card> highCards = Arrays.asList(cards).stream()
								.filter(c -> c.getValue() != cList.get(0).getValue()).collect(Collectors.toList());
						Integer[] hCards = get5Highest(highCards);
						return new FourOfAKind(cList.get(0).getValue(), new HighCard(hCards[2]));
					}
				}
			}
		}
		return null;
	}

	private FullHouse checkFullHouse(Card[] cards) {
		ThreeOfAKind threeOfAKind = checkThreeOfAKind(cards);
		if (Objects.nonNull(threeOfAKind)) {
			List<Card> leftCards = Arrays.asList(cards).stream()
					.filter(card -> card.getValue() != threeOfAKind.getValue()).collect(Collectors.toList());
			Pair pair = checkPair(leftCards.toArray(new Card[4]));
			if (Objects.nonNull(pair)) {
				return new FullHouse(threeOfAKind, pair);
			}
		}
		return null;
	}

	private Straight checkStraight(Card[] cards) {

		List<Card> sortedCards = Arrays.asList(cards).stream()
				.sorted((c1, c2) -> Integer.compare(c1.getValue(), c2.getValue())).collect(Collectors.toList());

		List<Card> str8List = new ArrayList<>();

		// check Bicycle
		if (sortedCards.get(sortedCards.size() - 1).getValue() == 12) {
			str8List.add(sortedCards.get(sortedCards.size() - 1));
			for (int i = 0; i < sortedCards.size(); i++) {
				if (sortedCards.get(i).getValue() == str8List.get(str8List.size() - 1).getValue() + 1) {
					str8List.add(sortedCards.get(i));
				} else {
					if (sortedCards.size() >= i + 2
							&& sortedCards.get(i + 1).getValue() == str8List.get(str8List.size() - 1).getValue() + 1) {
						str8List.add(sortedCards.get(i + 1));
						i++;
					} else {
						if (sortedCards.size() >= i + 3 && sortedCards.get(i + 2)
								.getValue() == str8List.get(str8List.size() - 1).getValue() + 1) {
							str8List.add(sortedCards.get(i + 2));
							i += 2;
						}
					}
				}
			}
		}
		if (!(str8List.size() > 4)) {
			str8List.clear();
			for (int j = 0; j < sortedCards.size(); j++) {
				str8List.add(sortedCards.get(j));
				for (int i = j + 1; i < sortedCards.size(); i++) {
					if (sortedCards.get(i).getValue() == str8List.get(str8List.size() - 1).getValue() + 1) {
						str8List.add(sortedCards.get(i));
					}
				}
				if (str8List.size() > 4) {
					break;
				} else {
					str8List.clear();
				}
			}
		}
		if (str8List.size() > 4) {
			if (str8List.size() == 5) {
				return new Straight(str8List.toArray(new Card[5]));
			}
			return new Straight(str8List.get(str8List.size() - 5), str8List.get(str8List.size() - 4),
					str8List.get(str8List.size() - 3), str8List.get(str8List.size() - 2),
					str8List.get(str8List.size() - 1));
		}
		return null;
	}

	private ThreeOfAKind checkThreeOfAKind(Card[] cards) {
		List<Card> cList = new ArrayList<>();
		for (int i = 0; i < cards.length; i++) {
			cList.clear();
			cList.add(cards[i]);
			for (int j = i + 1; j < cards.length; j++) {
				if (cards[j].getValue() == cards[i].getValue()) {
					cList.add(cards[j]);
					if (cList.size() == 3) {
						List<Card> highCards = Arrays.asList(cards);
						List<Card> fourCards = new ArrayList<>();
						for (Card c : highCards) {
							if (!(c.equals(cList.get(0)) || c.equals(cList.get(1)) || c.equals(cList.get(2)))) {
								fourCards.add(c);
							}
						}
						Integer[] hCards = get5Highest(fourCards);
						return new ThreeOfAKind(cList.get(0).getValue(), new HighCard(hCards[3]),
								new HighCard(hCards[2]));
					}
				}
			}
		}
		return null;
	}

	private TwoPair checkTwoPair(List<Card> cards, Pair pair) {
		Pair pair2 = checkPair(cards.toArray(new Card[5]));
		if (Objects.nonNull(pair2)) {
			Pair fst = pair;
			Pair snd = pair2;
			if (pair.getValue() > pair2.getValue()) {

			} else {
				Pair tmp = fst;
				fst = pair2;
				snd = tmp;
			}
			List<Card> highCards = cards.stream().filter(c -> c.getValue() != pair2.getValue())
					.sorted((c1, c2) -> Integer.compare(c1.getValue(), c2.getValue())).collect(Collectors.toList());
			return new TwoPair(fst, snd, new HighCard(highCards.get(2).getValue()));
		}
		return null;
	}

	private Pair checkPair(Card[] cards) {

		for (int i = 0; i < cards.length; i++) {
			Card pairCard0 = cards[i];
			for (int j = i + 1; j < cards.length; j++) {
				if (cards[j].getValue() == pairCard0.getValue()) {
					Card pairCard1 = cards[j];
					List<HighCard> highCards = Arrays.asList(cards).stream()
							.filter(c -> !(c.equals(pairCard0) || c.equals(pairCard1)))
							.map(a -> new HighCard(a.getValue()))
							.sorted((c1, c2) -> Integer.compare(c1.getValue0(), c2.getValue0()))
							.collect(Collectors.toList());
					return new Pair(pairCard0.getValue(), highCards.toArray(new HighCard[2]));
				}
			}
		}
		return null;

	}

	private HighCard checkHighCard(Card[] cards) {
		Integer[] highCards = get5Highest(Arrays.asList(cards));
		return new HighCard(highCards[4].intValue(), highCards[3].intValue(), highCards[2].intValue(),
				highCards[1].intValue(), highCards[0].intValue());
	}

	private Integer[] get5Highest(List<Card> cards) {
		List<Integer> cardValues = cards.stream().map(Card::getValue).sorted().collect(Collectors.toList());
		while (cardValues.size() > 5) {
			cardValues.remove(0);
		}
		return cardValues.toArray(new Integer[5]);
	}

}
