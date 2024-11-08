import java.io.*;
import java.util.*;

class ASynthesis{
	public static void main(String[] args)throws IOException,FileNotFoundException {
		BufferedReader br1=new BufferedReader(new FileReader("src/Littab.txt"));
		BufferedReader br2=new BufferedReader(new FileReader("src/SymTab.txt"));
		HashMap<String,Integer> SymH=new HashMap<String,Integer>();
		HashMap<Integer,Integer> LitH=new HashMap<Integer,Integer>();
		//Creating Hashmaps
		String s=null;
		while((s=br2.readLine())!=null) {
			String words[]=s.split(" ");
			SymH.put(words[0], Integer.parseInt(words[1]));
		}
		int i=0;
		while((s=br1.readLine())!=null) {
			String words[]=s.split(" ");
			LitH.put(i++, Integer.parseInt(words[1]));
		}
        
		br1.close();
		br2.close();
		BufferedWriter bw=new BufferedWriter(new FileWriter("src/machCode.txt",true));
		
		BufferedReader br=new BufferedReader(new FileReader("src/InterMedcode.txt"));
		while((s=br.readLine())!=null) {
			
			if(s.contains("(AD")) {
				//no need to process anthing
			}else if(s.contains("(IS")) {
				
				if(s.contains("10") || s.contains("09"))
					
					bw.write("+ "+s.substring(4,6).trim()+" 0 "+SymH.get(s.substring(11,12))+" ");
				else {
					
					if (s.contains("(L,")) {
						if(s.contains("(1)") || s.contains("(2)") || s.contains("(3)") ) {
							bw.write("+ "+s.substring(4,6).trim()+" "+ s.substring(9,10)+" "+LitH.get(Integer.parseInt(s.substring(14,s.length()-2))));
						}
						
					}
					if(s.contains("(S,")){
						if(s.contains("(1)") || s.contains("(2)") || s.contains("(3)") ) {
							bw.write("+ "+s.substring(4,6).trim()+" "+ s.substring(9,10)+" "+SymH.get(s.substring(14,s.length()-2)));
						}
						
										
					}
				}
			}else {
				if(s.contains("01")) {
					bw.write("+ 00 0 "+s.substring(11,s.length()-2));
				}
			}
			bw.write("\n");
			
		}
		br.close();
		bw.close();
		
		
	}
}