LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY flipFlopDPET IS
  PORT (clk, D:     IN STD_LOGIC;
        nSet, nRst: IN STD_LOGIC;
        Q, nQ:      OUT STD_LOGIC);
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

LIBRARY ieee;
USE ieee.std_logic_1164.all;

LIBRARY simpleLogic;
USE simpleLogic.all;

ENTITY binCounter_6bit IS
  PORT (nRst: IN STD_LOGIC;
        clk:  IN STD_LOGIC;
        c:    OUT STD_LOGIC_VECTOR (5 DOWNTO 0));
END binCounter_6bit;

ARCHITECTURE structure OF binCounter_6bit IS
  SIGNAL pD1, pD2, pD3, pD4: STD_LOGIC;
  SIGNAL iD1, iD2, iD3, iD4, iD5: STD_LOGIC;
  SIGNAL iQ0, iQ1, iQ2, iQ3, iQ4, iQ5: STD_LOGIC;
  SIGNAL inQ0: STD_LOGIC;
  COMPONENT gateAnd2
    PORT (x1, x2: IN STD_LOGIC;
          y:      OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT gateXor2
    PORT (x1, x2: IN STD_LOGIC;
          y:      OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT flipFlopDPET
    PORT (clk, D:     IN STD_LOGIC;
          nSet, nRst: IN STD_LOGIC;
          Q, nQ:      OUT STD_LOGIC);
  END COMPONENT;
BEGIN
  ad1: gateAnd2 PORT MAP (iQ0, iQ1, pD1);
  ad2: gateAnd2 PORT MAP (pD1, iQ2, pD2);
  ad3: gateAnd2 PORT MAP (pD2, iQ3, pD3);
  ad4: gateAnd2 PORT MAP (pD3, iQ4, pD4);
  xr1: gateXor2 PORT MAP (iQ0, iQ1, iD1);
  xr2: gateXor2 PORT MAP (pD1, iQ2, iD2);
  xr3: gateXor2 PORT MAP (pD2, iQ3, iD3);
  xr4: gateXor2 PORT MAP (pD3, iQ4, iD4);
  xr5: gateXor2 PORT MAP (pD4, iQ5, iD5);
  ff0: flipFlopDPET PORT MAP (clk, inQ0, '1', nRst, iQ0, inQ0);
  ff1: flipFlopDPET PORT MAP (clk, iD1,  '1', nRst, iQ1);
  ff2: flipFlopDPET PORT MAP (clk, iD2,  '1', nRst, iQ2);
  ff3: flipFlopDPET PORT MAP (clk, iD3,  '1', nRst, iQ3);
  ff4: flipFlopDPET PORT MAP (clk, iD4,  '1', nRst, iQ4);
  ff5: flipFlopDPET PORT MAP (clk, iD5,  '1', nRst, iQ5);
  c(0) <= iQ0;
  c(1) <= iQ1;
  c(2) <= iQ2;
  c(3) <= iQ3;
  c(4) <= iQ4;
  c(5) <= iQ5;
END structure;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY parReg_5bit IS
  PORT (nRst: IN STD_LOGIC;
        clk: IN STD_LOGIC;
        D: IN STD_LOGIC_VECTOR (4 DOWNTO 0);
        Q: OUT STD_LOGIC_VECTOR (4 DOWNTO 0));
END parReg_5bit;

ARCHITECTURE structure OF parReg_5bit IS
  COMPONENT flipFlopDPET
    PORT (clk, D: IN STD_LOGIC;
          nSet, nRst: IN STD_LOGIC;
          Q, nQ: OUT STD_LOGIC);
  END COMPONENT;
BEGIN
  ff0: flipFlopDPET PORT MAP (clk, D(0), '1', nRst, Q(0));
  ff1: flipFlopDPET PORT MAP (clk, D(1), '1', nRst, Q(1));
  ff2: flipFlopDPET PORT MAP (clk, D(2), '1', nRst, Q(2));
  ff3: flipFlopDPET PORT MAP (clk, D(3), '1', nRst, Q(3));
  ff4: flipFlopDPET PORT MAP (clk, D(4), '1', nRst, Q(4));
END structure;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY parReg_6bit IS
  PORT (nSet: IN STD_LOGIC;
        clk: IN STD_LOGIC;
        D: IN STD_LOGIC_VECTOR (5 DOWNTO 0);
        Q: OUT STD_LOGIC_VECTOR (5 DOWNTO 0));
END parReg_6bit;

ARCHITECTURE structure OF parReg_6bit IS
  COMPONENT flipFlopDPET
    PORT (clk, D: IN STD_LOGIC;
          nSet, nRst: IN STD_LOGIC;
          Q, nQ: OUT STD_LOGIC);
  END COMPONENT;
BEGIN
  ff0: flipFlopDPET PORT MAP (clk, D(0), nSet, '1', Q(0));
  ff1: flipFlopDPET PORT MAP (clk, D(1), nSet, '1', Q(1));
  ff2: flipFlopDPET PORT MAP (clk, D(2), nSet, '1', Q(2));
  ff3: flipFlopDPET PORT MAP (clk, D(3), nSet, '1', Q(3));
  ff4: flipFlopDPET PORT MAP (clk, D(4), nSet, '1', Q(4));
  ff5: flipFlopDPET PORT MAP (clk, D(5), nSet, '1', Q(5));
END structure;
