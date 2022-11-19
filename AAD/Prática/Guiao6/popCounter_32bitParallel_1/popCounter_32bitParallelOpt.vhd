LIBRARY ieee;
USE ieee.std_logic_1164.all;

LIBRARY arithmetic;
USE arithmetic.all;

ENTITY popCounter_32bitParallelOpt IS
  PORT (d: IN STD_LOGIC_VECTOR (31 DOWNTO 0);
        c: OUT STD_LOGIC_VECTOR (5 DOWNTO 0));
END popCounter_32bitParallelOpt;

ARCHITECTURE structure OF popCounter_32bitParallelOpt IS
  SIGNAL iSg0: STD_LOGIC_VECTOR (31 DOWNTO 0);
  SIGNAL iSg1: STD_LOGIC_VECTOR (23 DOWNTO 0);
  SIGNAL iSg2: STD_LOGIC_VECTOR (15 DOWNTO 0);
  SIGNAL iSg3: STD_LOGIC_VECTOR (9 DOWNTO 0);
  COMPONENT halfAdder_1bit
    PORT (a, cI: IN STD_LOGIC;
          s, cO:   OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT fullAdderWoCI_2bit
    PORT (a, b: IN STD_LOGIC_VECTOR (1 DOWNTO 0);
          s: OUT STD_LOGIC_VECTOR (1 DOWNTO 0);
          cO: OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT fullAdderWoCI_3bit
    PORT (a, b: IN STD_LOGIC_VECTOR (2 DOWNTO 0);
          s: OUT STD_LOGIC_VECTOR (2 DOWNTO 0);
          cO: OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT fullAdderWoCI_4bit
    PORT (a, b: IN STD_LOGIC_VECTOR (3 DOWNTO 0);
          s: OUT STD_LOGIC_VECTOR (3 DOWNTO 0);
          cO: OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT fullAdderWoCI_5bit
    PORT (a, b: IN STD_LOGIC_VECTOR (4 DOWNTO 0);
          s: OUT STD_LOGIC_VECTOR (4 DOWNTO 0);
          cO: OUT STD_LOGIC);
  END COMPONENT;
BEGIN
-- stage 0
  hA00:  halfAdder_1bit PORT MAP (d(1),  d(0),  iSg0(0),  iSg0(1));
  hA01:  halfAdder_1bit PORT MAP (d(3),  d(2),  iSg0(2),  iSg0(3));
  hA02:  halfAdder_1bit PORT MAP (d(5),  d(4),  iSg0(4),  iSg0(5));
  hA03:  halfAdder_1bit PORT MAP (d(7),  d(6),  iSg0(6),  iSg0(7));
  hA04:  halfAdder_1bit PORT MAP (d(9),  d(8),  iSg0(8),  iSg0(9));
  hA05:  halfAdder_1bit PORT MAP (d(11), d(10), iSg0(10), iSg0(11));
  hA06:  halfAdder_1bit PORT MAP (d(13), d(12), iSg0(12), iSg0(13));
  hA07:  halfAdder_1bit PORT MAP (d(15), d(14), iSg0(14), iSg0(15));
  hA08:  halfAdder_1bit PORT MAP (d(17), d(16), iSg0(16), iSg0(17));
  hA09:  halfAdder_1bit PORT MAP (d(19), d(18), iSg0(18), iSg0(19));
  hA010: halfAdder_1bit PORT MAP (d(21), d(20), iSg0(20), iSg0(21));
  hA011: halfAdder_1bit PORT MAP (d(23), d(22), iSg0(22), iSg0(23));
  hA012: halfAdder_1bit PORT MAP (d(25), d(24), iSg0(24), iSg0(25));
  hA013: halfAdder_1bit PORT MAP (d(27), d(26), iSg0(26), iSg0(27));
  hA014: halfAdder_1bit PORT MAP (d(29), d(28), iSg0(28), iSg0(29));
  hA015: halfAdder_1bit PORT MAP (d(31), d(30), iSg0(30), iSg0(31));
-- stage 1
  fA10: fullAdderWoCI_2bit PORT MAP (iSg0(1 DOWNTO 0),   iSg0(3 DOWNTO 2),   iSg1(1 DOWNTO 0),   iSg1(2));
  fA11: fullAdderWoCI_2bit PORT MAP (iSg0(5 DOWNTO 4),   iSg0(7 DOWNTO 6),   iSg1(4 DOWNTO 3),   iSg1(5));
  fA12: fullAdderWoCI_2bit PORT MAP (iSg0(9 DOWNTO 8),   iSg0(11 DOWNTO 10), iSg1(7 DOWNTO 6),   iSg1(8));
  fA13: fullAdderWoCI_2bit PORT MAP (iSg0(13 DOWNTO 12), iSg0(15 DOWNTO 14), iSg1(10 DOWNTO 9),  iSg1(11));
  fA14: fullAdderWoCI_2bit PORT MAP (iSg0(17 DOWNTO 16), iSg0(19 DOWNTO 18), iSg1(13 DOWNTO 12), iSg1(14));
  fA15: fullAdderWoCI_2bit PORT MAP (iSg0(21 DOWNTO 20), iSg0(23 DOWNTO 22), iSg1(16 DOWNTO 15), iSg1(17));
  fA16: fullAdderWoCI_2bit PORT MAP (iSg0(25 DOWNTO 24), iSg0(27 DOWNTO 26), iSg1(19 DOWNTO 18), iSg1(20));
  fA17: fullAdderWoCI_2bit PORT MAP (iSg0(29 DOWNTO 28), iSg0(31 DOWNTO 30), iSg1(22 DOWNTO 21), iSg1(23));
-- stage 2
  fA20: fullAdderWoCI_3bit PORT MAP (iSg1(2 DOWNTO 0),   iSg1(5 DOWNTO 3),   iSg2(2 DOWNTO 0),   iSg2(3));
  fA21: fullAdderWoCI_3bit PORT MAP (iSg1(8 DOWNTO 6),   iSg1(11 DOWNTO 9),  iSg2(6 DOWNTO 4),   iSg2(7));
  fA22: fullAdderWoCI_3bit PORT MAP (iSg1(14 DOWNTO 12), iSg1(17 DOWNTO 15), iSg2(10 DOWNTO 8),  iSg2(11));
  fA23: fullAdderWoCI_3bit PORT MAP (iSg1(20 DOWNTO 18), iSg1(23 DOWNTO 21), iSg2(14 DOWNTO 12), iSg2(15));
-- stage 3
  fA30: fullAdderWoCI_4bit PORT MAP (iSg2(3 DOWNTO 0),  iSg2(7 DOWNTO 4),   iSg3(3 DOWNTO 0),   iSg3(4));
  fA31: fullAdderWoCI_4bit PORT MAP (iSg2(11 DOWNTO 8), iSg2(15 DOWNTO 12), iSg3(8 DOWNTO 5),   iSg3(9));
-- stage 4
  fA40: fullAdderWoCI_5bit PORT MAP (iSg3(4 DOWNTO 0),   iSg3(9 DOWNTO 5), c(4 DOWNTO 0), c(5));
END structure;
