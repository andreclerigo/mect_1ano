library ieee;
use ieee.std_logic_1164.all;

library logic;
use logic.all;

library storeDev;
use storeDev.all;

library bitEncoder;
use bitEncoder.all;

library controlUnit;
use controlUnit.all;

ENTITY serialEncoder is
	PORT (mIn, nGRst, clk : in std_logic;
			busy : out std_logic;
			Y : out std_logic_vector(7 downto 0));
END serialEncoder;

ARCHITECTURE structure OF serialEncoder IS
	
	COMPONENT flipFlopDPET
		PORT (clk, D, nSet, nRst : in std_logic;
				Q, nQ : out std_logic);
	END COMPONENT;
	
	COMPONENT binCounter_3bit
		PORT (nRst : in std_logic;
				clk : in std_logic;
				c : out std_logic_vector(2 downto 0));
	END COMPONENT;
	
	COMPONENT bitEncoder
		PORT (bIn, mult, clk, nRst : in std_logic;
				bOut : out std_logic);
	END COMPONENT;
	
	COMPONENT parReg_8bit
		PORT (nSet, clk : in std_logic;
				D : in std_logic_vector(7 downto 0);
				Q : out std_logic_vector(7 downto 0));
	END COMPONENT;
	
	COMPONENT controlUnit
		PORT (nGRst, clk : in std_logic;
				add : in std_logic_vector(2 downto 0);
				kVals : out std_logic_vector(7 downto 0);
				nRst, busy : out std_logic);
	END COMPONENT;
	
	SIGNAL sig_mIn, sig_nreset, sig_busy : std_logic;
	SIGNAL sig_kvals : std_logic_vector(7 downto 0);
	SIGNAL sig_out : std_logic_vector(7 downto 0);
	SIGNAL sig_state : std_logic_vector(2 downto 0);
BEGIN
	ff0: flipFlopDPET PORT MAP (clk, mIn, '1', sig_nreset, sig_mIn);
	bnc: binCounter_3bit PORT MAP (sig_nreset, clk, sig_state);
	con: controlUnit PORT MAP (nGRst, clk, sig_state, sig_kvals, sig_nreset, sig_busy);

	-- Encode each bit
	be0: bitEncoder PORT MAP (sig_mIn, sig_kvals(0), clk, sig_nreset, sig_out(0));
	be1: bitEncoder PORT MAP (sig_mIn, sig_kvals(1), clk, sig_nreset, sig_out(1));
	be2: bitEncoder PORT MAP (sig_mIn, sig_kvals(2), clk, sig_nreset, sig_out(2));
	be3: bitEncoder PORT MAP (sig_mIn, sig_kvals(3), clk, sig_nreset, sig_out(3));
	be4: bitEncoder PORT MAP (sig_mIn, sig_kvals(4), clk, sig_nreset, sig_out(4));
	be5: bitEncoder PORT MAP (sig_mIn, sig_kvals(5), clk, sig_nreset, sig_out(5));
	be6: bitEncoder PORT MAP (sig_mIn, sig_kvals(6), clk, sig_nreset, sig_out(6));
	be7: bitEncoder PORT MAP (sig_mIn, sig_kvals(7), clk, sig_nreset, sig_out(7));
	
	-- Store bits to Register
	pr8: parReg_8bit PORT MAP ('1', clk, sig_out, Y);	
	busy <= sig_busy;
END structure;