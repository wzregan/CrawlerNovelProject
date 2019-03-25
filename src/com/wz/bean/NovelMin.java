package com.wz.bean;

public class NovelMin {
	private int PPID;

	private int PID;
	private int CAP;
	private long UPT;
	
	public NovelMin(int ppid,int pID, int cAP, long uPT) {
		PPID=ppid;
		PID = pID;
		CAP = cAP;
		UPT = uPT;
	}
	
	
	public int getPID() {
		return PID;
	}
	public void setPID(int pID) {
		PID = pID;
	}
	public int getCAP() {
		return CAP;
	}
	

	public void setCAP(int cAP) {
		CAP = cAP;
	}
	public long getUPT() {
		return UPT;
	}
	public void setUPT(long uPT) {
		UPT = uPT;
	}
	public int getPPID() {
		return PPID;
	}
	public void setPPID(int pPID) {
		PPID = pPID;
	}

}
