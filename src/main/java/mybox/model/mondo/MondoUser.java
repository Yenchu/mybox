package mybox.model.mondo;

import mybox.model.User;

public class MondoUser extends User {

	private Account account;
	
	private String token;

	public MondoUser() {
	}
	
	public MondoUser(Account account, String token) {
		this.account = account;
		this.token = token;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}