LIBRARY ieee;
USE ieee.std_logic_1164.all;

LIBRARY simpleLogic;
USE simpleLogic.all;

ENTITY halfAdder_1bit IS
  PORT (a, cI:  IN STD_LOGIC;
        s, cO:  OUT STD_LOGIC);
END halfAdder_1bit;

ARCHITECTURE structure OF halfAdder_1bit IS
  COMPONENT gateAnd2
    PORT (x0, x1: IN STD_LOGIC;
          y: OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT gateXOr2
    PORT (x0, x1: IN STD_LOGIC;
          y: OUT STD_LOGIC);
  END COMPONENT;
BEGIN
  xor20: gateXOr2 PORT MAP (cI, a, s);
  and20: gateAnd2 PORT MAP (cI, a, cO);
END structure;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

LIBRARY simpleLogic;
USE simpleLogic.all;

ENTITY fullAdder_1bit IS
  PORT (a, b, cI: IN STD_LOGIC;
        s, cO: OUT STD_LOGIC);
END fullAdder_1bit;

ARCHITECTURE structure OF fullAdder_1bit IS
  SIGNAL z0, z1, z2: STD_LOGIC;
  COMPONENT gateAnd2
    PORT (x0, x1: IN STD_LOGIC;
          y: OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT gateOr2
    PORT (x0, x1: IN STD_LOGIC;
          y: OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT gateXOr2
    PORT (x0, x1: IN STD_LOGIC;
          y: OUT STD_LOGIC);
  END COMPONENT;
BEGIN
  xor20: gateXOr2 PORT MAP (a, b, z0);
  xor21: gateXOr2 PORT MAP (cI, z0, s);
  and20: gateAnd2 PORT MAP (cI, z0, z1);
  and21: gateAnd2 PORT MAP (a, b, z2);
  or20:  gateOr2  PORT MAP (z1, z2, cO);
END structure;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

LIBRARY simpleLogic;
USE simpleLogic.all;

ENTITY fullAdderCLA_1bit IS
  PORT (a, b, cI: IN STD_LOGIC;
        s, cO: OUT STD_LOGIC);
END fullAdderCLA_1bit;

ARCHITECTURE structure OF fullAdderCLA_1bit IS
  SIGNAL z0: STD_LOGIC;
  COMPONENT gateAnd2
    PORT (x0, x1: IN STD_LOGIC;
          y: OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT gateXOr2
    PORT (x0, x1: IN STD_LOGIC;
          y: OUT STD_LOGIC);
  END COMPONENT;
BEGIN
  xor20: gateXOr2 PORT MAP (a, b, z0);
  xor21: gateXOr2 PORT MAP (cI, z0, s);
  and20: gateAnd2 PORT MAP (a, b, cO);
END structure;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY fullAdderWoCI_2bit IS
  PORT (a, b: IN STD_LOGIC_VECTOR (1 DOWNTO 0);
        s: OUT STD_LOGIC_VECTOR (1 DOWNTO 0);
        cO: OUT STD_LOGIC);
END fullAdderWoCI_2bit;

ARCHITECTURE structure OF fullAdderWoCI_2bit IS
  SIGNAL z0: STD_LOGIC;
  COMPONENT halfAdder_1bit
    PORT (a, cI: IN STD_LOGIC;
          s, cO:   OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT fullAdderCLA_1bit
    PORT (a, b, cI: IN STD_LOGIC;
          s, cO: OUT STD_LOGIC);
  END COMPONENT;
BEGIN
  hA0: halfAdder_1bit    PORT MAP (a(0), b(0), s(0), z0);
  fA0: fullAdderCLA_1bit PORT MAP (a(1), b(1), z0, s(1), cO);
END structure;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY fullAdderWoCI_3bit IS
  PORT (a, b: IN STD_LOGIC_VECTOR (2 DOWNTO 0);
        s: OUT STD_LOGIC_VECTOR (2 DOWNTO 0);
        cO: OUT STD_LOGIC);
END fullAdderWoCI_3bit;

ARCHITECTURE structure OF fullAdderWoCI_3bit IS
  SIGNAL z0, z1: STD_LOGIC;
  COMPONENT halfAdder_1bit
    PORT (a, cI: IN STD_LOGIC;
          s, cO:   OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT fullAdder_1bit
    PORT (a, b, cI: IN STD_LOGIC;
          s, cO: OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT fullAdderCLA_1bit
    PORT (a, b, cI: IN STD_LOGIC;
          s, cO: OUT STD_LOGIC);
  END COMPONENT;
BEGIN
  hA0:  halfAdder_1bit    PORT MAP (a(0), b(0), s(0), z0);
  fA10: fullAdder_1bit    PORT MAP (a(1), b(1), z0, s(1), z1);
  fA11: fullAdderCLA_1bit PORT MAP (a(2), b(2), z1, s(2), cO);
END structure;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY fullAdderWoCI_4bit IS
  PORT (a, b: IN STD_LOGIC_VECTOR (3 DOWNTO 0);
        s: OUT STD_LOGIC_VECTOR (3 DOWNTO 0);
        cO: OUT STD_LOGIC);
END fullAdderWoCI_4bit;

ARCHITECTURE structure OF fullAdderWoCI_4bit IS
  SIGNAL z0, z1, z2: STD_LOGIC;
  COMPONENT halfAdder_1bit
    PORT (a, cI: IN STD_LOGIC;
          s, cO:   OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT fullAdder_1bit
    PORT (a, b, cI: IN STD_LOGIC;
          s, cO: OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT fullAdderCLA_1bit
    PORT (a, b, cI: IN STD_LOGIC;
          s, cO: OUT STD_LOGIC);
  END COMPONENT;
BEGIN
  hA0:  halfAdder_1bit    PORT MAP (a(0), b(0), s(0), z0);
  fA10: fullAdder_1bit    PORT MAP (a(1), b(1), z0, s(1), z1);
  fA11: fullAdder_1bit    PORT MAP (a(2), b(2), z1, s(2), z2);
  fA12: fullAdderCLA_1bit PORT MAP (a(3), b(3), z2, s(3), cO);
END structure;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY fullAdderWoCI_5bit IS
  PORT (a, b: IN STD_LOGIC_VECTOR (4 DOWNTO 0);
        s: OUT STD_LOGIC_VECTOR (4 DOWNTO 0);
        cO: OUT STD_LOGIC);
END fullAdderWoCI_5bit;

ARCHITECTURE structure OF fullAdderWoCI_5bit IS
  SIGNAL z0, z1, z2, z3: STD_LOGIC;
  COMPONENT halfAdder_1bit
    PORT (a, cI: IN STD_LOGIC;
          s, cO:   OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT fullAdder_1bit
    PORT (a, b, cI: IN STD_LOGIC;
          s, cO: OUT STD_LOGIC);
  END COMPONENT;
  COMPONENT fullAdderCLA_1bit
    PORT (a, b, cI: IN STD_LOGIC;
          s, cO: OUT STD_LOGIC);
  END COMPONENT;
BEGIN
  hA0:  halfAdder_1bit    PORT MAP (a(0), b(0), s(0), z0);
  fA10: fullAdder_1bit    PORT MAP (a(1), b(1), z0, s(1), z1);
  fA11: fullAdder_1bit    PORT MAP (a(2), b(2), z1, s(2), z2);
  fA12: fullAdder_1bit    PORT MAP (a(3), b(3), z2, s(3), z3);
  fA13: fullAdderCLA_1bit PORT MAP (a(4), b(4), z3, s(4), cO);
END structure;
