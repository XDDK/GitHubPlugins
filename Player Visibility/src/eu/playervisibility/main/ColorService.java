package eu.playervisibility.main;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

public class ColorService {

	private static boolean debug = false;
	
	public static String replaceCodeWithCorrectColor(String configProperty) {
		
		String result = "";
		if(debug){
			System.out.println("[DEBUG] PROPRIETATE DE MODIFICAT A.I. SA ARATE CULORILE NICE: " + configProperty);
		}
		
		Map<String, ChatColor>literaCifraCuloare = new HashMap<String, ChatColor>();
		literaCifraCuloare.put("0", ChatColor.BLACK);
		literaCifraCuloare.put("1", ChatColor.DARK_BLUE);
		literaCifraCuloare.put("2", ChatColor.DARK_GREEN);
		literaCifraCuloare.put("3", ChatColor.DARK_AQUA);
		literaCifraCuloare.put("4", ChatColor.DARK_RED);
		literaCifraCuloare.put("5", ChatColor.DARK_PURPLE);
		literaCifraCuloare.put("6", ChatColor.GOLD);
		literaCifraCuloare.put("7", ChatColor.GRAY);
		literaCifraCuloare.put("8", ChatColor.DARK_GRAY);
		literaCifraCuloare.put("9", ChatColor.BLUE);
		// add letters
		literaCifraCuloare.put("a",  ChatColor.GREEN);
		literaCifraCuloare.put("b",  ChatColor.AQUA);
		literaCifraCuloare.put("c",  ChatColor.RED);
		literaCifraCuloare.put("d",  ChatColor.LIGHT_PURPLE);
		literaCifraCuloare.put("e",  ChatColor.YELLOW);
		literaCifraCuloare.put("f",  ChatColor.WHITE);
		// add effects
		literaCifraCuloare.put("k",  ChatColor.MAGIC);
		literaCifraCuloare.put("l",  ChatColor.BOLD);
		literaCifraCuloare.put("m",  ChatColor.STRIKETHROUGH);
		literaCifraCuloare.put("n",  ChatColor.UNDERLINE);
		literaCifraCuloare.put("o",  ChatColor.ITALIC);
		literaCifraCuloare.put("r",  ChatColor.RESET);
		
		String segmente[] = configProperty.split("&");
		
		for(int i=0; i<segmente.length; i++){
			
			if(segmente[i] != null && !segmente[i].isEmpty()){
				result = result + literaCifraCuloare.get(segmente[i].substring(0, 1)) + segmente[i].substring(1);
					if(debug){
						System.out.println("[DEBUG] Rezultat = " + result);
						System.out.println("[DEBUG] SEGMENT = " + segmente[i] + " subsir: " + segmente[i].substring(0, 1));
					}
			}
		}
		if(debug){
			System.out.println("[DEBUG] FINAL RESULT: " + result);
		}
			return result;
	}
}
