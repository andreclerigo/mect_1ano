LIBRARY ieee;
USE ieee.std_logic_1164.all;
USE ieee.std_logic_arith.all;
USE ieee.std_logic_unsigned.all;

ENTITY contMem IS
	PORT (add : in std_logic_vector (2 downto 0);
			dOut : out std_logic_vector (9 downto 0));
END contMem;

ARCHITECTURE behavior OF contMem IS
BEGIN
	PROCESS (add)
		-- 8 Kvalues, nRst, busy, nSetO
		TYPE CMem IS ARRAY(0 TO 7) OF std_logic_vector (9 DOWNTO 0);
		VARIABLE prog: CMem := ("0000000010",		-- Initial State 	nRst = 1	busy = 0
										"0101010111",		-- K0: 01010101	nRst = 1	busy = 1
										"0011001111",		-- K1: 00110011   nRst = 1	busy = 1
										"0000111111",  	-- K2: 00001111	nRst = 1	busy = 1
										"1111111111",  	-- K3: 11111111	nRst = 1	busy = 1
										"0000000001",  	-- Reset 			nRst = 0	busy = 1
										"0000000011",		-- Dont' care
										"0000000011");  	-- Don't care
	VARIABLE pos: INTEGER;
	BEGIN
		pos := CONV_INTEGER (add);
		dOut <= prog(pos);
	END PROCESS;
END behavior;

-- Control Unit 
LIBRARY ieee;
USE ieee.std_logic_1164.all;

LIBRARY logic;
USE logic.all;

ENTITY controlUnit IS
	PORT (nGRst, clk: in std_logic;
			add: in std_logic_vector(2 downto 0);
			kVals: out std_logic_vector(7 downto 0);
			nRst, busy : out std_logic);
END controlUnit;

ARCHITECTURE structure OF controlUnit IS

	COMPONENT contMem
		PORT (add:  IN STD_LOGIC_VECTOR (2 DOWNTO 0);
				dOut: OUT STD_LOGIC_VECTOR (9 DOWNTO 0));
	END COMPONENT;

	COMPONENT NAND_2
		PORT (A, B : in std_logic;
				Y : out std_logic);
	END COMPONENT;

	COMPONENT NOR_2
		PORT (A, B : in std_logic;
				Y : out std_logic);
	END COMPONENT;

	SIGNAL cLines : std_logic_vector(9 DOWNTO 0);
	SIGNAL sig_nrst, sig_nsetO : std_logic;
BEGIN
	cMem: contMem   PORT MAP (add, cLines);
	nad1: NAND_2 PORT MAP (nGRst, cLines(1), sig_nrst);
	nad2: NAND_2 PORT MAP (clk, sig_nrst, nRst);
	
	kVals <= cLines(9 downto 2);
	busy <= cLines(0);
END structure;