package it.polimi.elet.selflet.ssh;

import org.apache.log4j.Logger;

import com.jcraft.jsch.SftpProgressMonitor;

public class SftpTransferMonitor implements SftpProgressMonitor {

	private static Logger log = Logger.getLogger(SftpProgressMonitor.class);

	private long count = 0;
	private long dimension = 0;
	private String src = "";
	private String dest = "";

	public SftpTransferMonitor() {

	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public long getDimension() {
		return dimension;
	}

	public void setDimension(long dimension) {
		this.dimension = dimension;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public boolean count(long count) {
		this.count += count;
		log.info("Starting transfer" + src + " ---> " + dest + " trasferiti " + this.count + " bytes");
		return true;
	}

	public void end() {
		log.info("Transfer ended");
	}

	public void init(int op, String src, String dest, long max) {
		this.count = 0;
		this.dimension = max;
		this.src = src;
		this.dest = dest;
		log.info("Starting transfering of " + src + " to  " + dest);
	}

}