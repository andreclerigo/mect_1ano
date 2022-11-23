-- XOR_2 entity and architecture definition
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY XOR_2 IS
	PORT (A, B : in std_logic;
		  Y : out std_logic);
END XOR_2;

ARCHITECTURE XOR_2_arch OF XOR_2 IS
BEGIN
	Y <= A xor B;
END XOR_2_arch;

-- XOR_3 entity and architecture definition 
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY XOR_3 IS
	PORT (A, B, C : in std_logic;
		  Y : out std_logic);
END XOR_3;

ARCHITECTURE XOR_3_arch OF XOR_3 IS

	COMPONENT XOR_2
	PORT (A, B : in std_logic;
		  Y : out std_logic);
	END COMPONENT;
	
	SIGNAL S1, S2 : std_logic;

BEGIN
	XOR_2_inst1 : XOR_2 PORT MAP (A, B, S1);
	XOR_2_inst2 : XOR_2 PORT MAP (S1, C, S2);
	Y <= S2;
END XOR_3_arch;

--  Hadamard encoder 4 to 8 entity and architecture definition

LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY parallelEncoder IS
	PORT (A, B, C, D : in std_logic;
		  Y0, Y1, Y2, Y3, Y4, Y5, Y6, Y7 : out std_logic);
END parallelEncoder;

ARCHITECTURE Hadamard_4_to_8_arch OF parallelEncoder IS

	COMPONENT XOR_2
	PORT (A, B : in std_logic;
		  Y : out std_logic);
	END COMPONENT;

	COMPONENT XOR_3
	PORT (A, B, C : in std_logic;
		  Y : out std_logic);
	END COMPONENT;
	
	-- x0 is A, x1 is D xor x0, x2 is C xor x0, x3 is D xor x2, x4 is B xor x0, x5 is D xor x4, x6 is C xor x4, x7 is A xor B xor x4
	SIGNAL x0, x1, x2, x3, x4, x5, x6, x7 : std_logic;

BEGIN
	x0 <= A;
	x1 <= D xor x0;
	x2 <= C xor x0;
	x3 <= D xor x2;
	x4 <= B xor x0;
	x5 <= D xor x4;
	x6 <= C xor x4;
	x7 <= A xor B xor x4;
	
	XOR_2_inst1 : XOR_2 PORT MAP (x0, x1, Y0);
	XOR_2_inst2 : XOR_2 PORT MAP (x2, x3, Y1);
	XOR_2_inst3 : XOR_2 PORT MAP (x4, x5, Y2);
	XOR_2_inst4 : XOR_2 PORT MAP (x6, x7, Y3);
	XOR_2_inst5 : XOR_2 PORT MAP (x0, x2, Y4);
	XOR_2_inst6 : XOR_2 PORT MAP (x1, x3, Y5);
	XOR_2_inst7 : XOR_2 PORT MAP (x4, x6, Y6);
	XOR_2_inst8 : XOR_2 PORT MAP (x5, x7, Y7);
	
END Hadamard_4_to_8_arch;






	
	

	