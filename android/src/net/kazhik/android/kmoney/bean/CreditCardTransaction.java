package net.kazhik.android.kmoney.bean;

public class CreditCardTransaction extends Transaction {
	private int cardId;

	public CreditCardTransaction(Transaction o) {
		super(o);
	}
	public CreditCardTransaction() {
	}
	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}

}
