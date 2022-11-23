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
-- Generated on "11/10/2022 17:13:57"
                                                             
-- Vhdl Test Bench(with test vectors) for design  :          controlUnit
-- 
-- Simulation tool : 3rd Party
-- 

LIBRARY ieee;                                               
USE ieee.std_logic_1164.all;                                

ENTITY controlUnit_vhd_vec_tst IS
END controlUnit_vhd_vec_tst;
ARCHITECTURE controlUnit_arch OF controlUnit_vhd_vec_tst IS
-- constants                                                 
-- signals                                                   
SIGNAL add : STD_LOGIC_VECTOR(2 DOWNTO 0);
SIGNAL clk : STD_LOGIC;
SIGNAL clkO : STD_LOGIC;
SIGNAL kVals : STD_LOGIC_VECTOR(7 DOWNTO 0);
SIGNAL nGRst : STD_LOGIC;
SIGNAL nRst : STD_LOGIC;
SIGNAL nSetO : STD_LOGIC;
COMPONENT controlUnit
	PORT (
	add : IN STD_LOGIC_VECTOR(2 DOWNTO 0);
	clk : IN STD_LOGIC;
	clkO : OUT STD_LOGIC;
	kVals : OUT STD_LOGIC_VECTOR(7 DOWNTO 0);
	nGRst : IN STD_LOGIC;
	nRst : OUT STD_LOGIC;
	nSetO : OUT STD_LOGIC
	);
END COMPONENT;
BEGIN
	i1 : controlUnit
	PORT MAP (
-- list connections between master ports and signals
	add => add,
	clk => clk,
	clkO => clkO,
	kVals => kVals,
	nGRst => nGRst,
	nRst => nRst,
	nSetO => nSetO
	);

-- nGRst
t_prcs_nGRst: PROCESS
BEGIN
	nGRst <= '1';
WAIT;
END PROCESS t_prcs_nGRst;
-- add[2]
t_prcs_add_2: PROCESS
BEGIN
	add(2) <= '0';
	WAIT FOR 80000 ps;
	add(2) <= '1';
	WAIT FOR 80000 ps;
	add(2) <= '0';
WAIT;
END PROCESS t_prcs_add_2;
-- add[1]
t_prcs_add_1: PROCESS
BEGIN
	add(1) <= '0';
	WAIT FOR 40000 ps;
	add(1) <= '1';
	WAIT FOR 40000 ps;
	add(1) <= '0';
	WAIT FOR 40000 ps;
	add(1) <= '1';
	WAIT FOR 40000 ps;
	add(1) <= '0';
WAIT;
END PROCESS t_prcs_add_1;
-- add[0]
t_prcs_add_0: PROCESS
BEGIN
	add(0) <= '0';
	WAIT FOR 20000 ps;
	add(0) <= '1';
	WAIT FOR 20000 ps;
	add(0) <= '0';
	WAIT FOR 20000 ps;
	add(0) <= '1';
	WAIT FOR 20000 ps;
	add(0) <= '0';
	WAIT FOR 20000 ps;
	add(0) <= '1';
	WAIT FOR 20000 ps;
	add(0) <= '0';
	WAIT FOR 20000 ps;
	add(0) <= '1';
	WAIT FOR 20000 ps;
	add(0) <= '0';
WAIT;
END PROCESS t_prcs_add_0;

-- clk
t_prcs_clk: PROCESS
BEGIN
LOOP
	clk <= '0';
	WAIT FOR 10000 ps;
	clk <= '1';
	WAIT FOR 10000 ps;
	IF (NOW >= 200000 ps) THEN WAIT; END IF;
END LOOP;
END PROCESS t_prcs_clk;
END controlUnit_arch;
