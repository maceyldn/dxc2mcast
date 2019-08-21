package uk.co.andymace.radio.dxc2mcast;

public class DXSpot {

	public String opcode;
	public String freq;
	public String spotted_call;
	public String date;
	public String time;
	public String comment;
	public String fullspotter;
	public String cluster;
	public String spotter_call;
	public String spottersid;
	public boolean extended;
	public String spotter_ip;
	public String hops;
	public String spotter_host;
	public String PCmessage;
	
	@Override
	public String toString() {
		
		if (extended)
		{
			return "DXSpot [freq=" + freq + ", spotted_call=" + spotted_call + ", date=" + date
					+ ", time=" + time + ", comment=" + comment + ", cluster=" + cluster + ", spotter_call=" + spotter_call
					+ ", spottersid=" + spottersid + ", spotter_ip=" + spotter_ip + ", spotter_host=" + spotter_host + "]";
		} else {
		return "DXSpot [freq=" + freq + ", spotted_call=" + spotted_call + ", date=" + date
				+ ", time=" + time + ", comment=" + comment + ", cluster=" + cluster + ", spotter_call=" + spotter_call
				+ ", spottersid=" + spottersid + "]";
			}
		}

}
