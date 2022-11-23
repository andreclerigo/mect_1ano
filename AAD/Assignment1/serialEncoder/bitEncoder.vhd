library IEEE;
use IEEE.std_logic_1164.all;

library storeDev;
use storeDev.all;

library logic;
use logic.all;

ENTITY bitEncoder IS
	PORT (bIn, mult, clk, nRst : in std_logic;
			bOut : out std_logic);
END bitEncoder;

ARCHITECTURE structure of bitEncoder is
	
	COMPONENT flipFlopDPET IS
		PORT (clk, D, nSet, nRst: in std_logic;
				Q : out std_logic);
	END COMPONENT;
	
	COMPONENT AND_2 IS
		PORT (A, B: in std_logic;
				Y : out std_logic);
	END COMPONENT;
	
	COMPONENT XOR_2 IS
		PORT (A, B: in std_logic;
				Y : out std_logic);
	END COMPONENT;
	
	SIGNAL s_xor, s_ff, s_and : std_logic;
BEGIN
	and0: AND_2 PORT MAP (bIn, mult, s_and);
	xor0: XOR_2 PORT MAP (s_and, s_ff, s_xor);
	ff0 : flipFlopDPET PORT MAP (clk, s_xor, '1', nRst, s_ff);
	
	bOut <= s_xor;
END structure;