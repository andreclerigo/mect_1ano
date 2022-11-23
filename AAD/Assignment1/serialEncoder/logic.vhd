-- AND_2 entity and architecture definition
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY AND_2 IS
	PORT (A, B : in std_logic;
			Y : out std_logic);
END AND_2;

ARCHITECTURE structure OF AND_2 IS
BEGIN
	Y <= A and B;
END structure;

-- NAND_2 entity and architecture definition
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY NAND_2 IS
	PORT (A, B : in std_logic;
			Y : out std_logic);
END NAND_2;

ARCHITECTURE structure OF NAND_2 IS
BEGIN
	Y <= A nand B;
END structure;

-- NOR_2 entity and architecture definition
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY NOR_2 IS
	PORT (A, B : in std_logic;
			Y : out std_logic);
END NOR_2;

ARCHITECTURE structure OF NOR_2 IS
BEGIN
	Y <= A nor B;
END structure;

-- XOR_2 entity and architecture definition
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY XOR_2 IS
	PORT (A, B : in std_logic;
			Y : out std_logic);
END XOR_2;

ARCHITECTURE structure OF XOR_2 IS
BEGIN
	Y <= A xor B;
END structure;