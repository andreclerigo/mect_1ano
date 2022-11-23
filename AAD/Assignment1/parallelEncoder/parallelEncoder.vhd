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
		  Y: out std_logic_vector(7 downto 0));
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
	
	SIGNAL sig_x2, sig_x4: std_logic;

BEGIN
	Y(0) <= D;															-- x0 = m4
	
	XOR_M1_M4: XOR_2 PORT MAP (A, D, Y(1));					-- x1 = m1 xor m4
	XOR_M2_M4: XOR_2 PORT MAP (B, D, sig_x2);					-- x2 = m2 xor m4
	XOR_M1_X2: XOR_2 PORT MAP (A, sig_x2, Y(3)); 			-- x3 = m1 xor x2
	XOR_M3_M4: XOR_2 PORT MAP (C, D, sig_x4);					-- x4 = m3 xor m4
	XOR_M1_X4: XOR_2 PORT MAP (A, sig_x4, Y(5));				-- x5 = m1 xor x4
	XOR_M2_X4: XOR_2 PORT MAP (B, sig_x4, Y(6));				-- x6 = m2 xor x4
	XOR_M1_M2_X4: XOR_3 PORT MAP (A, B, sig_x4, Y(7));		-- x7 = m1 xor m2 xor x4
	
	Y(2) <= sig_x2;
	Y(4) <= sig_x4;
END Hadamard_4_to_8_arch;
	