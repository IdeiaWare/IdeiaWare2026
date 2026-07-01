package br.unisc.toolkit.entity;

import java.util.List;

public class PointOfViewInfo {

	private int id;
	private int personaID;
	private String personasID;
	private String names;
	private String user;
	private String need;
	private String insight;
	private int povID;
	
	public PointOfViewInfo() {
    }
 
    public PointOfViewInfo(int id, String names, int personaID, String personasID, String user, String need, String insight, int povID) {
        this.id = id;
        this.names = names;
        this.personaID = personaID;
        this.personasID = personasID;
        this.user = user;
        this.need = need;
        this.insight = insight;
        this.povID = povID;        
    }
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPersonaID() {
		return personaID;
	}
	public void setPersonaID(int personaID) {
		this.personaID = personaID;
	}
	public int getPovID() {
		return povID;
	}
	public void setPovID(int povID) {
		this.povID = povID;
	}
	public String getNames() {
		return names;
	}
	public void setNames(String names) {
		this.names = names;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getNeed() {
		return need;
	}
	public void setNeed(String need) {
		this.need = need;
	}
	public String getInsight() {
		return insight;
	}
	public void setInsight(String insight) {
		this.insight = insight;
	}

	public String getPersonasID() {
		return personasID;
	}

	public void setPersonasID(String personasID) {
		this.personasID = personasID;
	}
}
