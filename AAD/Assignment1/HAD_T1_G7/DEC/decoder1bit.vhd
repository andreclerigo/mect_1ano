library ieee;
use ieee.std_logic_1164.all;

library logic;
use logic.all;

ENTITY decoder1bit IS
	PORT (C0, C1, C2, C3: in std_logic;
			B: out std_logic;							-- bit value
			V: out std_logic);						-- valid flag
END decoder1bit;

ARCHITECTURE structure OF decoder1bit IS

	COMPONENT NOT_1
		PORT (A : in std_logic;
				Y : out std_logic);
	END COMPONENT;

	COMPONENT AND_2
		PORT (A, B : in std_logic;
				Y : out std_logic);
	END COMPONENT;
	
	COMPONENT AND_3
		PORT (A, B, C : in std_logic;
				Y : out std_logic);
	END COMPONENT;
	
	COMPONENT OR_2
		PORT (A, B : in std_logic;
				Y : out std_logic);
	END COMPONENT;
	
	COMPONENT NOR_2
		PORT (A, B : in std_logic;
				Y : out std_logic);
	END COMPONENT;
	
	COMPONENT XOR_2
		PORT (A, B : in std_logic;
				Y : out std_logic);
	END COMPONENT;
	
	SIGNAL sig_and0, sig_and1, sig_nor0, sig_nor1, sig_or0, sig_or1 : std_logic;
BEGIN
	and0: AND_2 PORT MAP (C3, C2, sig_and0);
	and1: AND_2 PORT MAP (C1, C0, sig_and1);
	or0: OR_2 PORT MAP (sig_and0, sig_and1, sig_or0);
	
	nor0: NOR_2 PORT MAP (C3, C2, sig_nor0);
	nor1: NOR_2 PORT MAP (C1, C0, sig_nor1);
	or1: OR_2 PORT MAP (sig_nor0, sig_nor1, sig_or1);
	
	xnr0: XOR_2 PORT MAP (sig_or0, sig_or1, V);
	
	B <= sig_or0;
END structure;