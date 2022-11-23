-- FlipFlopD Positive Edge Trigger
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY flipFlopDPET IS
	PORT (clk, D, nSet, nRst : in std_logic;
			Q, nQ : out std_logic);
END flipFlopDPET;

ARCHITECTURE behavior OF flipFlopDPET IS
BEGIN
	PROCESS (clk, nSet, nRst)
	BEGIN
		IF (nRst = '0')
			THEN Q <= '0';
				nQ <= '1';
		ELSIF (nSet = '0')
			THEN Q <= '1';
				nQ <= '0';
		ELSIF (clk = '1') AND (clk'EVENT)
			THEN Q <= D;
				nQ <= NOT D;
		END IF;
	END PROCESS;
END behavior;

-- Bit Counter with 3 bits
LIBRARY ieee;
USE ieee.std_logic_1164.all;

LIBRARY logic;
USE logic.all;

ENTITY binCounter_3bit IS
	PORT (nRst : in std_logic;
			clk : in std_logic;
			c : out std_logic_vector(2 downto 0));
END binCounter_3bit;

ARCHITECTURE structure OF binCounter_3bit IS

	COMPONENT AND_2
		PORT (A, B : in std_logic;
				Y : out std_logic);
	END COMPONENT;

	COMPONENT XOR_2
		PORT (A, B : in std_logic;
				Y : out std_logic);
	END COMPONENT;

	COMPONENT flipFlopDPET
		PORT (clk, D : in std_logic;
				nSet, nRst : in std_logic;
				Q, nQ : out std_logic);
	END COMPONENT;

	SIGNAL pD1, iD1, iD2, iQ0, iQ1, iQ2, inQ0: std_logic;
BEGIN
	and0: AND_2 PORT MAP (iQ0, iQ1, pD1);
	xor0: XOR_2 PORT MAP (iQ0, iQ1, iD1);
	xor1: XOR_2 PORT MAP (pD1, iQ2, iD2);

	ff0: flipFlopDPET PORT MAP (clk, inQ0, '1', nRst, iQ0, inQ0);
	ff1: flipFlopDPET PORT MAP (clk, iD1,  '1', nRst, iQ1);
	ff2: flipFlopDPET PORT MAP (clk, iD2,  '1', nRst, iQ2);

	c(0) <= iQ0;
	c(1) <= iQ1;
	c(2) <= iQ2;
END structure;

-- Parallel Register of 8bit
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY parReg_8bit IS
	PORT (nSet, clk : in std_logic;
			D : in std_logic_vector(7 downto 0);
			Q : out std_logic_vector(7 downto 0));
END parReg_8bit;

ARCHITECTURE structure OF parReg_8bit IS

	COMPONENT flipFlopDPET
		PORT (clk, D, nSet, nRst : in std_logic;
				Q, nQ : out std_logic);
	END COMPONENT;
  
BEGIN
	ff0: flipFlopDPET PORT MAP (clk, D(0), nSet, '1', Q(0));
	ff1: flipFlopDPET PORT MAP (clk, D(1), nSet, '1', Q(1));
	ff2: flipFlopDPET PORT MAP (clk, D(2), nSet, '1', Q(2));
	ff3: flipFlopDPET PORT MAP (clk, D(3), nSet, '1', Q(3));
	ff4: flipFlopDPET PORT MAP (clk, D(4), nSet, '1', Q(4));
	ff5: flipFlopDPET PORT MAP (clk, D(5), nSet, '1', Q(5));
	ff6: flipFlopDPET PORT MAP (clk, D(6), nSet, '1', Q(6));
	ff7: flipFlopDPET PORT MAP (clk, D(7), nSet, '1', Q(7));
END structure;