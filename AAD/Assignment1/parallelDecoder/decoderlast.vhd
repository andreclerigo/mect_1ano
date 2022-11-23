library ieee;
use ieee.std_logic_1164.all;

library logic;
use logic.all;

ENTITY decoderlast IS
	PORT (Y0, Y1, Y2 : in std_logic;
			m1, m2 : in std_logic;
			B : out std_logic);
END decoderlast;

ARCHITECTURE structure OF decoderlast IS

	COMPONENT AND_2
		PORT (A, B : in std_logic;
				Y : out std_logic);
	END COMPONENT;
	
	COMPONENT OR_2
		PORT (A, B : in std_logic;
				Y : out std_logic);
	END COMPONENT;
	
	COMPONENT XOR_2
		PORT (A, B : in std_logic;
				Y : out std_logic);
	END COMPONENT;
	
	SIGNAL sig_b, sig_c, sig_aux1, sig_aux2, sig_aux3: std_logic;
	
BEGIN
	xor0: XOR_2 PORT MAP (Y1, m1, sig_b);					-- b = y1 xor m1
	xor1: XOR_2 PORT MAP (Y2, m2, sig_c);					-- c = y2 xor m2
	and0: AND_2 PORT MAP (Y0, sig_b, sig_aux1);			-- sig_aux1 = ab
	or0: OR_2 PORT MAP (Y0, sig_b, sig_aux2);				-- sig_aux2 = a+b
	and1: AND_2 PORT MAP (sig_c, sig_aux2, sig_aux3);  -- sig_aux3 = z(x+y)
	or1: OR_2 PORT MAP (sig_aux1, sig_aux3, B);
END structure;