package awesome.BiscuitsAndGravy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class EdgarMine {

	// Bob Swan private final static String corpOfficerCIK = "0001218363";
	private final static String corpOfficerCIK = "0001559658";
	private final static String edgarURL = "https://www.sec.gov/cgi-bin/own-disp?action=getowner&CIK=" + corpOfficerCIK;
	private final static String attribute = "transaction-report";
	
	public static void main(String[] args) throws IOException {

		Document jDoc = Jsoup.connect(edgarURL).get();
		String officerName = jDoc.title().substring(23, jDoc.title().length()).replace(' ', '_');
		Element el = jDoc.getElementById(attribute);
		int counter = 0;
		
		ArrayList<Integer> purchases = new ArrayList<Integer>();
		ArrayList<Integer> sales = new ArrayList<Integer>();
		ArrayList<Integer> totalHeld = new ArrayList<Integer>();
		ArrayList<String> dates = new ArrayList<String>();
		
		Boolean soldStock = true;
		String date = null;
		int sold = 0;
		int bought = 0;
		int total = 0;
		for (String str : el.toString().split("\n")) {
			if ( str.contains("FFE0E0") || str.contains("FFFFFF") ) {
				if ( str.contains("FFFFFF") ) {
					soldStock = false;
				}
				++counter;
			} else if ( (counter > 0) && (counter < 8) ) {
				if (counter == 2) {
					date = str.substring(14, str.length()-6);
				}
				++counter;
			} else if (counter == 8) {
				++counter;
				int index = str.indexOf("right");
				int shares = (int) Double.parseDouble(str.substring(index+7, str.length()-6));
				if (soldStock) {
					sold = shares;
				} else {
					bought = shares;
				}
				soldStock = true;
			} else if (counter == 9) {
				++counter;
				int index = str.indexOf("right");
				int shares = (int) Double.parseDouble(str.substring(index+7, str.length()-6));
				total = shares;
			} else if (counter > 9) {
				if (counter == 12) {
					counter = -1;
					if (str.substring(19, str.length()-6).equals("Common Stock")) {
						sales.add(sold);
						purchases.add(bought);
						totalHeld.add(total);
						dates.add(date);
					}
					sold = 0;
					bought = 0;
					total = 0;
					date = null;
				}
				++counter;
			}
		}
        StringBuilder sb = new StringBuilder();
        sb.append("Date,Bought,Sold,Total Holdings\n");
        for (int i=0; i<sales.size(); i++) {
        	sb.append(dates.get(i));
        	sb.append(',');
        	sb.append(purchases.get(i).toString());
        	sb.append(',');
        	sb.append(sales.get(i).toString());
        	sb.append(',');
        	sb.append(totalHeld.get(i).toString());
        	sb.append('\n');
        }
        PrintWriter pw = new PrintWriter(new File(officerName + ".csv"));
        pw.write(sb.toString());
        pw.close();
	}
}
