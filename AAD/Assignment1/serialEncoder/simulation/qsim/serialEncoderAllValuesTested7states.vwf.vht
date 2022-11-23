-- Copyright (C) 2020  Intel Corporation. All rights reserved.
-- Your use of Intel Corporation's design tools, logic functions 
-- and other software and tools, and any partner logic 
-- functions, and any output files from any of the foregoing 
-- (including device programming or simulation files), and any 
-- associated documentation or information are expressly subject 
-- to the terms and conditions of the Intel Program License 
-- Subscription Agreement, the Intel Quartus Prime License Agreement,
-- the Intel FPGA IP License Agreement, or other applicable license
-- agreement, including, without limitation, that your use is for
-- the sole purpose of programming logic devices manufactured by
-- Intel and sold by Intel or its authorized distributors.  Please
-- refer to the applicable agreement for further details, at
-- https://fpgasoftware.intel.com/eula.

-- *****************************************************************************
-- This file contains a Vhdl test bench with test vectors .The test vectors     
-- are exported from a vector file in the Quartus Waveform Editor and apply to  
-- the top level entity of the current Quartus project .The user can use this   
-- testbench to simulate his design using a third-party simulation tool .       
-- *****************************************************************************
-- Generated on "11/11/2022 19:02:18"
                                                             
-- Vhdl Test Bench(with test vectors) for design  :          serialEncoder
-- 
-- Simulation tool : 3rd Party
-- 

LIBRARY ieee;                                               
USE ieee.std_logic_1164.all;                                

ENTITY serialEncoder_vhd_vec_tst IS
END serialEncoder_vhd_vec_tst;
ARCHITECTURE serialEncoder_arch OF serialEncoder_vhd_vec_tst IS
-- constants                                                 
-- signals                                                   
SIGNAL busy : STD_LOGIC;
SIGNAL clk : STD_LOGIC;
SIGNAL kVals : STD_LOGIC_VECTOR(7 DOWNTO 0);
SIGNAL mIn : STD_LOGIC;
SIGNAL nGRst : STD_LOGIC;
SIGNAL s_controls : STD_LOGIC_VECTOR(2 DOWNTO 0);
SIGNAL s_states : STD_LOGIC_VECTOR(2 DOWNTO 0);
SIGNAL Y : STD_LOGIC_VECTOR(7 DOWNTO 0);
COMPONENT serialEncoder
	PORT (
	busy : BUFFER STD_LOGIC;
	clk : IN STD_LOGIC;
	kVals : BUFFER STD_LOGIC_VECTOR(7 DOWNTO 0);
	mIn : IN STD_LOGIC;
	nGRst : IN STD_LOGIC;
	s_controls : BUFFER STD_LOGIC_VECTOR(2 DOWNTO 0);
	s_states : BUFFER STD_LOGIC_VECTOR(2 DOWNTO 0);
	Y : BUFFER STD_LOGIC_VECTOR(7 DOWNTO 0)
	);
END COMPONENT;
BEGIN
	i1 : serialEncoder
	PORT MAP (
-- list connections between master ports and signals
	busy => busy,
	clk => clk,
	kVals => kVals,
	mIn => mIn,
	nGRst => nGRst,
	s_controls => s_controls,
	s_states => s_states,
	Y => Y
	);

-- clk
t_prcs_clk: PROCESS
BEGIN
LOOP
	clk <= '0';
	WAIT FOR 10000 ps;
	clk <= '1';
	WAIT FOR 10000 ps;
	IF (NOW >= 2000000 ps) THEN WAIT; END IF;
END LOOP;
END PROCESS t_prcs_clk;

-- mIn
t_prcs_mIn: PROCESS
BEGIN
	mIn <= '0';
	WAIT FOR 130000 ps;
	mIn <= '1';
	WAIT FOR 20000 ps;
	mIn <= '0';
	WAIT FOR 120000 ps;
	mIn <= '1';
	WAIT FOR 20000 ps;
	mIn <= '0';
	WAIT FOR 80000 ps;
	mIn <= '1';
	WAIT FOR 40000 ps;
	mIn <= '0';
	WAIT FOR 120000 ps;
	mIn <= '1';
	WAIT FOR 20000 ps;
	mIn <= '0';
	WAIT FOR 60000 ps;
	mIn <= '1';
	WAIT FOR 20000 ps;
	mIn <= '0';
	WAIT FOR 20000 ps;
	mIn <= '1';
	WAIT FOR 20000 ps;
	mIn <= '0';
	WAIT FOR 80000 ps;
	mIn <= '1';
	WAIT FOR 40000 ps;
	mIn <= '0';
	WAIT FOR 60000 ps;
	mIn <= '1';
	WAIT FOR 60000 ps;
	mIn <= '0';
	WAIT FOR 120000 ps;
	mIn <= '1';
	WAIT FOR 20000 ps;
	mIn <= '0';
	WAIT FOR 40000 ps;
	mIn <= '1';
	WAIT FOR 20000 ps;
	mIn <= '0';
	WAIT FOR 40000 ps;
	mIn <= '1';
	WAIT FOR 20000 ps;
	mIn <= '0';
	WAIT FOR 60000 ps;
	mIn <= '1';
	WAIT FOR 20000 ps;
	mIn <= '0';
	WAIT FOR 20000 ps;
	mIn <= '1';
	WAIT FOR 20000 ps;
	mIn <= '0';
	WAIT FOR 40000 ps;
	mIn <= '1';
	WAIT FOR 40000 ps;
	mIn <= '0';
	WAIT FOR 20000 ps;
	mIn <= '1';
	WAIT FOR 20000 ps;
	mIn <= '0';
	WAIT FOR 80000 ps;
	mIn <= '1';
	WAIT FOR 40000 ps;
	mIn <= '0';
	WAIT FOR 40000 ps;
	mIn <= '1';
	WAIT FOR 20000 ps;
	mIn <= '0';
	WAIT FOR 20000 ps;
	mIn <= '1';
	WAIT FOR 40000 ps;
	mIn <= '0';
	WAIT FOR 60000 ps;
	mIn <= '1';
	WAIT FOR 60000 ps;
	mIn <= '0';
	WAIT FOR 40000 ps;
	mIn <= '1';
	WAIT FOR 80000 ps;
	mIn <= '0';
WAIT;
END PROCESS t_prcs_mIn;

-- nGRst
t_prcs_nGRst: PROCESS
BEGIN
	nGRst <= '1';
	WAIT FOR 60000 ps;
	nGRst <= '0';
	WAIT FOR 40000 ps;
	nGRst <= '1';
	WAIT FOR 300000 ps;
	nGRst <= '0';
	WAIT FOR 20000 ps;
	nGRst <= '1';
	WAIT FOR 200000 ps;
	nGRst <= '0';
	WAIT FOR 20000 ps;
	nGRst <= '1';
	WAIT FOR 160000 ps;
	nGRst <= '0';
	WAIT FOR 10000 ps;
	nGRst <= '1';
	WAIT FOR 150000 ps;
	nGRst <= '0';
	WAIT FOR 20000 ps;
	nGRst <= '1';
WAIT;
END PROCESS t_prcs_nGRst;
END serialEncoder_arch;
