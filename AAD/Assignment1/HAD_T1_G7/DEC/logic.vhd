-- NOT_1 entity and architecture definition
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY NOT_1 IS
	PORT (A : in std_logic;
		  Y : out std_logic);
END NOT_1;

ARCHITECTURE structure OF NOT_1 IS
BEGIN
	Y <= not A;
END structure;

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

-- AND_3 entity and architecture definition
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY AND_3 IS
	PORT (A, B, C : in std_logic;
		  Y : out std_logic);
END AND_3;

ARCHITECTURE structure OF AND_3 IS
BEGIN
	Y <= A and B and C;
END structure;

-- AND_4 entity and architecture definition
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY AND_4 IS
	PORT (A, B, C, D : in std_logic;
		  Y : out std_logic);
END AND_4;

ARCHITECTURE structure OF AND_4 IS
BEGIN
	Y <= A and B and C and D;
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

-- OR_2 entity and architecture definition
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY OR_2 IS
	PORT (A, B : in std_logic;
		  Y : out std_logic);
END OR_2;

ARCHITECTURE structure OF OR_2 IS
BEGIN
	Y <= A or B;
END structure;

-- OR_4 entity and architecture definition
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY OR_4 IS
	PORT (A, B, C, D : in std_logic;
		  Y : out std_logic);
END OR_4;

ARCHITECTURE structure OF OR_4 IS
BEGIN
	Y <= A or B or C or D;
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