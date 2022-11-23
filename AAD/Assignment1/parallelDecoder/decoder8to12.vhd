library ieee;
use ieee.std_logic_1164.all;

library logic;
use logic.all;

ENTITY decoder8to12 IS
	PORT (Y : in std_logic_vector(7 downto 0);
			C : out std_logic_vector(11 downto 0);
			Ys : out std_logic_vector(7 downto 0));
END decoder8to12;

ARCHITECTURE structure OF decoder8to12 IS

	COMPONENT XOR_2
		PORT (A, B : in std_logic;
				Y : out std_logic);
	END COMPONENT;

BEGIN
	c11: XOR_2 PORT MAP (Y(0), Y(1), C(0));	-- c11 = y0 xor y1
	c12: XOR_2 PORT MAP (Y(2), Y(3), C(1));	-- c12 = y2 xor y3
	c13: XOR_2 PORT MAP (Y(4), Y(5), C(2));	-- c13 = y4 xor y5
	c14: XOR_2 PORT MAP (Y(6), Y(7), C(3));	-- c14 = y6 xor y7

	c21: XOR_2 PORT MAP (Y(0), Y(2), C(4));	-- c21 = y0 xor y2
	c22: XOR_2 PORT MAP (Y(1), Y(3), C(5));	-- c22 = y1 xor y3
	c23: XOR_2 PORT MAP (Y(4), Y(6), C(6));	-- c23 = y4 xor y6
	c24: XOR_2 PORT MAP (Y(5), Y(7), C(7));	-- c24 = y5 xor y7

	c31: XOR_2 PORT MAP (Y(0), Y(4), C(8));	-- c31 = y0 xor y4
	c32: XOR_2 PORT MAP (Y(1), Y(5), C(9));	-- c32 = y1 xor y5
	c33: XOR_2 PORT MAP (Y(2), Y(6), C(10));	-- c33 = y2 xor y6
	c34: XOR_2 PORT MAP (Y(3), Y(7), C(11));	-- c34 = y3 xor y7
	
	Ys <= Y;
END structure;
