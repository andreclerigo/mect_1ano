LIBRARY ieee;
USE ieee.std_logic_1164.all;

LIBRARY control;
USE control.all;

LIBRARY storeDev;
USE storeDev.all;

LIBRARY arithmetic;
USE arithmetic.all;

LIBRARY simpleLogic;
USE simpleLogic.all;

ENTITY popCounter_32bitSerial IS
  PORT (nGRst: IN STD_LOGIC;
        clk:   IN STD_LOGIC;
        dIn:   IN STD_LOGIC;
        cnt:   OUT STD_LOGIC_VECTOR (5 DOWNTO 0));
END popCounter_32bitSerial;

ARCHITECTURE structure OF popCounter_32bitSerial IS
  SIGNAL iNSet, iNRst, iDIn, clkO: STD_LOGIC;
  SIGNAL tCntI: STD_LOGIC_VECTOR (4 DOWNTO 0);
  SIGNAL tCntO: STD_LOGIC_VECTOR (5 DOWNTO 0);
  SIGNAL stat:  STD_LOGIC_VECTOR (5 DOWNTO 0);
  COMPONENT flipFlopDPET
    PORT (clk, D: IN STD_LOGIC;
          nSet, nRst: IN STD_LOGIC;
          Q, nQ: OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT parReg_5bit
    PORT (nRst: IN STD_LOGIC;
          clk: IN STD_LOGIC;
          D: IN STD_LOGIC_VECTOR (4 DOWNTO 0);
          Q: OUT STD_LOGIC_VECTOR (4 DOWNTO 0));
  END COMPONENT;
  COMPONENT parReg_6bit
    PORT (nSet: IN STD_LOGIC;
          clk: IN STD_LOGIC;
          D: IN STD_LOGIC_VECTOR (5 DOWNTO 0);
          Q: OUT STD_LOGIC_VECTOR (5 DOWNTO 0));
  END COMPONENT;
  COMPONENT halfAdder_5bit
    PORT (a:  IN STD_LOGIC_VECTOR (4 DOWNTO 0);
          cI: IN STD_LOGIC;
          s:  OUT STD_LOGIC_VECTOR (4 DOWNTO 0);
          cO: OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT binCounter_6bit
    PORT (nRst: IN STD_LOGIC;
          clk:  IN STD_LOGIC;
          c:    OUT STD_LOGIC_VECTOR (5 DOWNTO 0));
  END COMPONENT;
  COMPONENT control
    PORT (nGRst: IN STD_LOGIC;
          clk:   IN STD_LOGIC;
          add:   IN STD_LOGIC_VECTOR (5 DOWNTO 0);
          nRst:  OUT STD_LOGIC;
          nSetO: OUT STD_LOGIC;
          clkO:  OUT STD_LOGIC);
  END COMPONENT;
BEGIN
  ff:  flipFlopDPET PORT MAP (clk, dIn, '1', iNRst, iDIn);
  hd:  halfAdder_5bit PORT MAP (tCntI, iDIn, tCntO(4 DOWNTO 0), tCntO(5));
  pr5: parReg_5bit PORT MAP (iNRst, clk, tCntO(4 DOWNTO 0), tCntI);
  pr6: parReg_6bit PORT MAP (iNSet, clkO, tCntO, cnt);
  bc:  binCounter_6bit PORT MAP (iNRst, clk, stat);
  con: control  PORT MAP (nGRst, clk, stat, iNRst, INSet, clkO);
END structure;
