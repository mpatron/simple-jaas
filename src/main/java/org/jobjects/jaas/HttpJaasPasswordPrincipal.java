package org.jobjects.jaas;

import java.io.Serializable;
import java.security.Principal;

public class HttpJaasPasswordPrincipal implements Principal, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;

	/**
	 * @param name
	 */
	public HttpJaasPasswordPrincipal(String name) {
		if (name == null) {
			throw new NullPointerException("NULL password.");
		}
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HttpJaasPasswordPrincipal other = (HttpJaasPasswordPrincipal) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "HttpJaasPasswordPrincipal [name=" + name + "]";
	}
}
