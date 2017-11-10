package co.reborncraft.syslogin_banmanager.api.objects;

import jdk.nashorn.internal.runtime.ParserException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;

public class Inet4AddressBlock {
	private long address = 0;
	private short blockSize = 32;

	public Inet4AddressBlock(InetAddress addr) throws ParseException {
		this(addr.getHostAddress());
	}

	public Inet4AddressBlock(String addrWithBlock) throws ParseException {
		addrWithBlock = addrWithBlock.replaceAll("\\s+", "");
		if (addrWithBlock.matches("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(/([0-9]|[12][0-9]|3[0-2]))?$")) {
			for (String octet : addrWithBlock.replaceAll("(/([0-9]|[12][0-9]|3[0-2]))$", "").split("\\.")) {
				address <<= 8;
				address += Integer.parseUnsignedInt(octet);
			}
			if (addrWithBlock.contains("/")) {
				blockSize = (short) Integer.parseUnsignedInt(addrWithBlock.substring(addrWithBlock.indexOf("/") + 1));
			}
		} else {
			throw new ParserException("Invalid IP address block: '" + addrWithBlock + "'");
		}
	}

	public long getAddressInt() {
		return address;
	}

	public short getBlockSize() {
		return blockSize;
	}

	public boolean intersects(Inet4AddressBlock addressBlock) {
		boolean foreignBlockIsBigger = addressBlock.getBlockSize() < blockSize;
		int biggestBlock = 32 - (foreignBlockIsBigger ? addressBlock.getBlockSize() : blockSize);
		long primaryInt = foreignBlockIsBigger ? addressBlock.getAddressInt() : address;
		long secondaryInt = foreignBlockIsBigger ? address : addressBlock.getAddressInt();
		return secondaryInt >= primaryInt && secondaryInt < primaryInt + (1 << biggestBlock);
	}

	public boolean isSubBlockOf(Inet4AddressBlock biggerBlock) {
		if (biggerBlock.getBlockSize() <= blockSize) {
			return biggerBlock.matches(this.minimum()) && biggerBlock.matches(this.maximum());
		}
		return false;
	}

	public InetAddress minimum() {
		try {
			return InetAddress.getByName(asAddressString(address));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public InetAddress maximum() {
		try {
			return InetAddress.getByName(asAddressString(address + (1 << (32 - blockSize)) - 1));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean matches(InetAddress address) {
		try {
			return this.intersects(new Inet4AddressBlock(address));
		} catch (ParseException e) {
			e.printStackTrace(); // Shouldn't happen
		}
		return false;
	}

	@Override
	public String toString() {
		return "Inet4AddressBlock[" + asAddressString() + "]";
	}

	public String asAddressString() {
		return asAddressString(address) + (blockSize < 32 ? "/" + blockSize : "");
	}

	public String asAddressString(long address) {
		return (address >> 24) + "." + ((address >> 16) % 256) + "." + ((address >> 8) % 256) + "." + ((address) % 256);
	}

	public boolean intersects(InetAddress addr) {
		try {
			return intersects(new Inet4AddressBlock(addr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
}
