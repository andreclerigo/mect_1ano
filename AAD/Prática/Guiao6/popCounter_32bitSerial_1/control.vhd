LIBRARY ieee;
USE ieee.std_logic_1164.all;
USE ieee.std_logic_arith.all;
USE ieee.std_logic_unsigned.all;

ENTITY contMem IS
  PORT (add:  IN STD_LOGIC_VECTOR (5 DOWNTO 0);
        dOut: OUT STD_LOGIC_VECTOR (2 DOWNTO 0));
END contMem;

ARCHITECTURE behavior OF contMem IS
BEGIN
  PROCESS (add)
    TYPE CMem IS ARRAY(0 TO 63) OF STD_LOGIC_VECTOR (2 DOWNTO 0);
    VARIABLE prog: CMem := (CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   00
                            CONV_STD_LOGIC_VECTOR (16#5#, 3),  -- nRst = 1   nSetO = 0   nEnClk = 1   01
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   02
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   03
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   04
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   05
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   06
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   07
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   08
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   09
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   0a
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   0b
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   0c
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   0d
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   0e
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   0f
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   10
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   11
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   12
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   13
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   14
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   15
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   16
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   17
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   18
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   19
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   1a
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   1b
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   1c
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   1d
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   1e
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   1f
                            CONV_STD_LOGIC_VECTOR (16#6#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 0   20
                            CONV_STD_LOGIC_VECTOR (16#3#, 3),  -- nRst = 0   nSetO = 1   nEnClk = 1   21
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   22
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   23
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   24
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   25
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   26
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   27
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   28
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   29
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   2a
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   2b
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   2c
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   2d
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   2e
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   2f
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   30
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   31
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   32
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   33
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   34
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   35
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   36
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   37
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   38
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   39
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   3a
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   3b
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   3c
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   3d
                            CONV_STD_LOGIC_VECTOR (16#7#, 3),  -- nRst = 1   nSetO = 1   nEnClk = 1   3e
                            CONV_STD_LOGIC_VECTOR (16#7#, 3)); -- nRst = 1   nSetO = 1   nEnClk = 1   3f
    VARIABLE pos: INTEGER;
  BEGIN
    pos := CONV_INTEGER (add);
    dOut <= prog(pos);
  END PROCESS;
END behavior;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

LIBRARY simpleLogic;
USE simpleLogic.all;

ENTITY control IS
  PORT (nGRst: IN STD_LOGIC;
        clk:   IN STD_LOGIC;
        add:   IN STD_LOGIC_VECTOR (5 DOWNTO 0);
        nRst:  OUT STD_LOGIC;
        nSetO: OUT STD_LOGIC;
        clkO:  OUT STD_LOGIC);
END control;

ARCHITECTURE structure OF control IS
  SIGNAL cLines: STD_LOGIC_VECTOR (2 DOWNTO 0);
  SIGNAL iNRst, iNSetO: STD_LOGIC;
  COMPONENT contMem
    PORT (add:  IN STD_LOGIC_VECTOR (5 DOWNTO 0);
          dOut: OUT STD_LOGIC_VECTOR (2 DOWNTO 0));
  END COMPONENT;
  COMPONENT gateNand2
    PORT (x1, x2: IN STD_LOGIC;
          y:      OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT gateNor2
    PORT (x1, x2: IN STD_LOGIC;
          y:      OUT STD_LOGIC);
  END COMPONENT;
BEGIN
  cMem: contMem   PORT MAP (add, cLines);
  nad1: gateNand2 PORT MAP (nGRst, cLines(2), iNRst);
  nad2: gateNand2 PORT MAP (clk, iNRst, nRst);
  nad3: gateNand2 PORT MAP (nGRst, cLines(1), iNSetO);
  nad4: gateNand2 PORT MAP (clk, iNSetO, nSetO);
  nord: gateNor2  PORT MAP (clk, cLines(0), clkO);
END structure;
