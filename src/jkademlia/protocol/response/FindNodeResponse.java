package jkademlia.protocol.response;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.KademliaProtocol;
import jkademlia.protocol.RPC;

public class FindNodeResponse extends RPC {

	public static final String[][] DATA_STRUCTURE = {
			{ "foundNodeID", "NODE_ID_LENGTH" },
			{ "ipAddress", "IP_ADDRESS_LENGTH" }, { "port", "PORT_LENGTH" } };

	public BigInteger foundNodeID;
	public BigInteger ipAddress;
	public BigInteger port;

	public static final int FOUND_NODE_AREA = KademliaProtocol.TOTAL_AREA_LENGTH;
	public static final int IP_ADDRESS_LENGTH = Integer.SIZE / 8;
	public static final int PORT_LENGTH = Integer.SIZE / 8;
	public static final int IP_AREA = FOUND_NODE_AREA + NODE_ID_LENGTH;
	public static final int PORT_AREA = IP_AREA + IP_ADDRESS_LENGTH;
	public static final int TOTAL_AREA_LENGTH = PORT_AREA + PORT_LENGTH;

	public BigInteger getFoundNodeID() {
		return foundNodeID;
	}

	public void setFoundNodeID(BigInteger foundNodeID)
			throws KademliaProtocolException {
		if (foundNodeID == null) {
			throw new KademliaProtocolException(
					"Cannot set foundNodeID to null");
		} else if (foundNodeID.bitLength() > KademliaProtocol.NODE_ID_LENGTH * 8) {
			throw new KademliaProtocolException("foundNodeID must have"
					+ KademliaProtocol.NODE_ID_LENGTH * 8 + "bits, found"
					+ foundNodeID.bitLength() + "bits");
		} else {
			this.foundNodeID = foundNodeID;
		}
	}

	public BigInteger getIpAddress() {
		return ipAddress;
	}

	public InetAddress getIpAddressInet() throws UnknownHostException {
		return InetAddress.getByAddress(ipAddress.toByteArray());
	}

	public void setIpAddress(BigInteger ipAddress)
			throws KademliaProtocolException {
		if (ipAddress == null) {
			throw new KademliaProtocolException("Cannot set ipAddress to null");
		} else if (ipAddress.bitLength() > FindNodeResponse.IP_ADDRESS_LENGTH * 8) {
			throw new KademliaProtocolException("ipAddress must have "
					+ (FindNodeResponse.IP_ADDRESS_LENGTH * 8) + " bits, "
					+ "found " + ipAddress.bitLength() + " bits");
		} else {
			this.ipAddress = ipAddress;
		}
	}

	public void setIpAddress(InetAddress ip) {
		try {
			this.setIpAddress(new BigInteger(ip.getAddress()));
		} catch (KademliaProtocolException e) {
			e.printStackTrace();
		}
	}

	public BigInteger getPort() {
		return port;
	}

	public Integer getPortInteger() {
		return this.getPort().intValue();
	}

	public void setPort(BigInteger port) throws KademliaProtocolException {
		if (port == null) {
			throw new KademliaProtocolException("Cannot set port to null");
		} else if (port.bitLength() > FindNodeResponse.PORT_LENGTH * 8) {
			throw new KademliaProtocolException("port must have "
					+ (FindNodeResponse.PORT_LENGTH * 8) + " bits, " + "found "
					+ port.bitLength() + " bits");
		} else {
			this.port = port;
		}
	}

	public void setPort(int port) {
		try {
			this.setPort(BigInteger.valueOf(port));
		} catch (KademliaProtocolException e) {
			e.printStackTrace();
		}
	}

	public String[][] getDataStructure() {
		return DATA_STRUCTURE;
	}

	@Override
	public int getInfoLength() {
		return TOTAL_AREA_LENGTH;
	}

	@Override
	public byte getType() {
		return FIND_NODE;
	}

	@Override
	public boolean isRequest() {
		return false;
	}

}
