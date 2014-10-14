package eu.playervisibility.main;

import org.bukkit.ChatColor;

public class ColorService {

	private static boolean debug = false;
	
	public static String replaceCodeWithCorrectColor(String configProperty) {
		
		String result = "";
		if(debug){
			System.out.println("[DEBUG] PROPRIETATE DE MODIFICAT A.I. SA ARATE CULORILE NICE: " + configProperty);
		}
		ChatColor culori[] = new ChatColor[10];
		culori[0] = ChatColor.BLACK;
		culori[1] = ChatColor.DARK_BLUE;
		culori[2] = ChatColor.DARK_GREEN;
		culori[3] = ChatColor.DARK_AQUA;
		culori[4] = ChatColor.DARK_RED;
		culori[5] = ChatColor.DARK_PURPLE;
		culori[6] = ChatColor.GOLD;
		culori[7] = ChatColor.GRAY;
		culori[8] = ChatColor.DARK_GRAY;
		culori[9] = ChatColor.BLUE;
		
		String segmente[] = configProperty.split("&");
		
		for(int i=0; i<segmente.length; i++){
			
			if(segmente[i] != null && !segmente[i].isEmpty()){
				int numar = Integer.valueOf(String.valueOf(segmente[i].charAt(0)));
					if(debug){
						System.out.println("NUMAR = " + numar);
						System.out.println("CULOAREA PE CARE AR TREBUI S-O PUNEM ESTE: " + culori[numar]);
						System.out.println("SEGMENT = " + segmente[i]);
					}
				result = result + culori[numar] + segmente[i].substring(1);
			}
		}
		if(debug){
			System.out.println("[DEBUG] FINAL RESULT: " + result);
		}
			return result;

	}
}
