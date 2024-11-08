import java.io.*;

class Symtab {
    String name;
    int addr;
    Symtab() {
        name = null;
        addr = 0;
    }
}

class Litab {
    String name;
    int addr;
    Litab() {
        name = null;
        addr = 0;
    }
}

class Optab {
    String name;
    String opcode;
    Optab(){
    	name=opcode=null;
    }
    Optab(String name, String opcode) {
        this.name = name;
        this.opcode = opcode;
    }
}

class Synthesis {
    String opcode;
    String[] operands;
    String addlo, dlno;
    String label;
    String lits;
    String regn;
    String adop;

    Synthesis() throws IOException, FileNotFoundException {
        opcode = addlo = regn = dlno = null;
        operands = new String[2];
        label = lits = null;
        Assembler.setup();
    }

    boolean isop(String x) {
        for (int i = 0; i < Assembler.ot.length; i++) {
        	
            if (Assembler.ot[i].name.equalsIgnoreCase(x)) {
                this.opcode = Assembler.ot[i].opcode;
                return true;
            }
        }
        return false;
    }

    boolean isadr(String x) {
        for (int i = 0; i < Assembler.adr.length; i++) {
            if (Assembler.adr[i].equalsIgnoreCase(x)) {
                adop = "0" + (i + 1);
                return true;
            }
        }
        return false;
    }

    boolean isdeclr(String x) {
        for (int i = 0; i < Assembler.declr.length; i++) {
            if (Assembler.declr[i].equalsIgnoreCase(x)) {
                dlno = "0" + (i + 1);
                return true;
            }
        }
        return false;
    }

    boolean isreg(String x) {
        for (int i = 0; i < Assembler.reg.length; i++) {
            if (Assembler.reg[i].equalsIgnoreCase(x)) {
                regn = (i == 0) ? "1" : (i == 1) ? "2" : "3";
                return true;
            }
        }
        return false;
    }

   

    void writeic(String x) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/InterMedCode.txt",true))) {
            bw.write(x + "\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    void writeSym() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/SymTab.txt"))) {
            for (int i = 0; i < Assembler.stab.length; i++) {
                if (Assembler.stab[i].name == null) 
                    break;
                bw.write(Assembler.stab[i].name + " " + Assembler.stab[i].addr + "\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    void writeLit() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/LitTab.txt"))) {
            for (int i = 0; i < Assembler.ltab.length; i++) {
                if (Assembler.ltab[i].name == null) 
                    break;
                bw.write(Assembler.ltab[i].name + " " + Assembler.ltab[i].addr + "\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    boolean isValidNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    void processS() {
        boolean end = false;
        String st, ics;
        String litt = null;
        int lit_it = 0;
        int sym_it = 0;
        int loc_cntr = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("src/input.txt"))) {
            while ((st = br.readLine()) != null && !end) {
            	int constant=0,x=0;
            	regn=litt=null;
                ics = null;
                st = st.replaceAll(",", " ");
                String[] words = st.split("\\s+");
                String type = null;
                int op_itr = 0;
                litt = null;
                for (int i = 0; i < words.length; i++) {
                    if (isop(words[i])) {
                        type = "IS";
                    } else if (isadr(words[i])) {
                        type = "AD";
                    } else if (isdeclr(words[i])) {
                        type = "DL";
                    } else if (isreg(words[i])) {
                        // already processed
                    } else if (words[i].matches("^'.*'$") && !type.equals("DL")) {
                        Assembler.ltab[lit_it] = new Litab();
                        String str = words[i];
                        Assembler.ltab[lit_it].name = str;
                        litt = ""+lit_it;
                        lit_it++;
                        
                    } else if(words[i].matches("^'.*'$") && type.equals("DL")) {
                    	constant=Integer.parseInt(words[i].substring(1,words[i].length()-1));
                    	
                    }
                    else {
                        // Symbol Handling
                        if (isValidNumber(words[i])) {  // Validate if the operand is a number
                            addlo = words[i];
                            if(type.equals("DL"))
                            	x=Integer.parseInt(words[i]);
                        } else {
                            if (i == 0 ) {
                                Assembler.stab[sym_it] = new Symtab();
                                Assembler.stab[sym_it].name = words[i];
                                Assembler.stab[sym_it].addr = loc_cntr;
                                sym_it++;
                                label = words[i];
                            } else {
                                operands[op_itr++] = words[i];
                            }
                        }
                    }
                }
                if (type.equals("IS")) {
                    loc_cntr += 1;
                    String opp = null;
                    if (regn == null) {
                        if (op_itr == 1) {
                            opp = "(S," + operands[0] + ") ";
                        }
                    } else {
                        if (litt == null)
                            opp = "(" + regn + ")" + "(S," + operands[0] + ") ";
                        else {
                            opp = "(" + regn + ")" + "(L," +litt + ") ";
                        }
                    }
                    if(opp==null)
                    	 ics = "(IS," + opcode + ") ";
                    else
                    	ics = "(IS," + opcode + ") " + opp;
                } else if (type.equals("AD")) {
                    switch (adop) {
                        case "01": // START
                            loc_cntr = loc_cntr + Integer.parseInt(addlo);
                            ics = "(AD," + adop + ") " + "(C," + addlo + ")";
                            break;
                        case "02": // END
                            ics = "(AD," + adop + ") ";
                            for (int i = 0; i < Assembler.ltab.length; i++) {
                                if (Assembler.ltab[i].name == null) {
                                    break;
                                }
                                if (Assembler.ltab[i].addr == 0) {
                                    Assembler.ltab[i].addr = loc_cntr;
                                    loc_cntr++;
                                }
                            }
                            break;
                        case "03": // LTORG
                            ics = "(AD," + adop + ") ";
                            for (int i = 0; i < Assembler.ltab.length; i++) {
                                if (Assembler.ltab[i].name == null) {
                                    break;
                                }
                                if (Assembler.ltab[i].addr == 0) {
                                    Assembler.ltab[i].addr = loc_cntr;
                                    loc_cntr++;
                                }
                            }
                            break;
                        case "04": // EQU
                            ics = "(AD," + adop + ") ";
                            break;
                    }
                }else {
    					//DL
    					if(dlno.equals("02")) {
    						
    						ics="(DL,"+dlno+") "+"(C,"+x+") ";
    						loc_cntr+=1;
    					}else {
    						ics="(DL,"+dlno+") "+"(C,"+constant+") ";
    						loc_cntr+=Integer.parseInt(addlo);
    					}					
    				}
                    
                
                writeic(ics);
                
            }
            writeSym();
            writeLit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class Assembler{
	static Optab[] ot;
	static String[] reg= {"AReg","BReg","CReg"};
	static String[] adr= {"START","END","LTORG","EQU"};
	static String[] copr= {"LT","LE","EQ","GE","GT","ANY"};
	static String[] declr= {"DC","DS"};
	static Symtab[] stab=new Symtab[25];
	static Litab[] ltab=new Litab[15];
	
	//setup
	static void setup()throws IOException,FileNotFoundException{
		String line;
		BufferedReader br=new BufferedReader(new FileReader("src/optab.txt"));
		int i=0;
		ot=new Optab[11];
	    for (int k = 0; k < stab.length; k++) {
	        stab[k] = new Symtab();  
	    }
	    for (int j = 0; j < ltab.length; j++) {
	        ltab[j] = new Litab();  
	    }
		while((line=br.readLine())!=null) {
			String[] word=line.split(" ");
			ot[i++]=new Optab(word[0],word[1]);
			
		}
		br.close();
		
	}	
	//main call
	public static void main(String[] args)throws IOException,FileNotFoundException{
		Synthesis s=new Synthesis();
		s.processS();		
		System.out.println("Passs 1 Completed\n");
	}
}