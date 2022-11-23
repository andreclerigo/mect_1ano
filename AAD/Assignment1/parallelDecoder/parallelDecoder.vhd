library ieee;
use ieee.std_logic_1164.all;

library logic;
use logic.all;

library decoder8to12;
use decoder8to12.all;

library decoder1bit;
use decoder1bit.all;

library decoderlast;
use decoderlast.all;

ENTITY parallelDecoder IS
	PORT (Y : in std_logic_vector(7 downto 0);
			M : out std_logic_vector(3 downto 0);
			V : out std_logic);
END parallelDecoder;

ARCHITECTURE structure OF parallelDecoder IS
	
	COMPONENT AND_3
		PORT (A, B, C : in std_logic;
				Y : out std_logic);
	END COMPONENT;
	
	COMPONENT decoder8to12
		PORT (Y: in std_logic_vector(7 downto 0);
				C: out std_logic_vector(11 downto 0));
	END COMPONENT;
	
	COMPONENT decoder1bit
		PORT (C0, C1, C2, C3: in std_logic;
				B: out std_logic;
				V: out std_logic);
	END COMPONENT;
	
	COMPONENT decoderlast
		PORT (Y0, Y1, Y2: in std_logic;
				m1, m2 : in std_logic;
				B: out std_logic);
	END COMPONENT;

	SIGNAL sig_c : std_logic_vector(11 downto 0);
	SIGNAL sig_v : std_logic_vector(2 downto 0);
	SIGNAL sig_y, sig_z, sig_m1, sig_m2, sig_aux1, sig_aux2, sig_aux3, sig_y0, sig_y1, sig_y2 : std_logic;
BEGIN
	d8to12: decoder8to12 PORT MAP (Y, sig_c);
	
	dm1 : decoder1bit PORT MAP (sig_c(0), sig_c(1), sig_c(2), sig_c(3), sig_m1, sig_v(0));
	dm2 : decoder1bit PORT MAP (sig_c(4), sig_c(5), sig_c(6), sig_c(7), sig_m2, sig_v(1));
	dm3 : decoder1bit PORT MAP (sig_c(8), sig_c(9), sig_c(10), sig_c(11), M(2), sig_v(2));
	
	M(0) <= sig_m1;
	M(1) <= sig_m2;
	last: decoderlast PORT MAP (Y(0), Y(1), Y(2), sig_m1, sig_m2, M(3));
	
	valid: AND_3 PORT MAP (sig_v(0), sig_v(1), sig_v(2), V);
END structure;