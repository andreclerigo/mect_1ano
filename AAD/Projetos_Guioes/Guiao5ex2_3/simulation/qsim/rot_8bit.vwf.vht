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
-- Generated on "11/18/2022 23:58:30"
                                                             
-- Vhdl Test Bench(with test vectors) for design  :          rot_8bit
-- 
-- Simulation tool : 3rd Party
-- 

LIBRARY ieee;                                               
USE ieee.std_logic_1164.all;                                

ENTITY rot_8bit_vhd_vec_tst IS
END rot_8bit_vhd_vec_tst;
ARCHITECTURE rot_8bit_arch OF rot_8bit_vhd_vec_tst IS
-- constants                                                 
-- signals                                                   
SIGNAL dIn : STD_LOGIC_VECTOR(7 DOWNTO 0);
SIGNAL dir : STD_LOGIC;
SIGNAL dOut : STD_LOGIC_VECTOR(7 DOWNTO 0);
SIGNAL sel : STD_LOGIC_VECTOR(2 DOWNTO 0);
COMPONENT rot_8bit
	PORT (
	dIn : IN STD_LOGIC_VECTOR(7 DOWNTO 0);
	dir : IN STD_LOGIC;
	dOut : BUFFER STD_LOGIC_VECTOR(7 DOWNTO 0);
	sel : IN STD_LOGIC_VECTOR(2 DOWNTO 0)
	);
END COMPONENT;
BEGIN
	i1 : rot_8bit
	PORT MAP (
-- list connections between master ports and signals
	dIn => dIn,
	dir => dir,
	dOut => dOut,
	sel => sel
	);
-- dIn[7]
t_prcs_dIn_7: PROCESS
BEGIN
	dIn(7) <= '1';
WAIT;
END PROCESS t_prcs_dIn_7;
-- dIn[6]
t_prcs_dIn_6: PROCESS
BEGIN
	dIn(6) <= '1';
WAIT;
END PROCESS t_prcs_dIn_6;
-- dIn[5]
t_prcs_dIn_5: PROCESS
BEGIN
	dIn(5) <= '0';
WAIT;
END PROCESS t_prcs_dIn_5;
-- dIn[4]
t_prcs_dIn_4: PROCESS
BEGIN
	dIn(4) <= '1';
WAIT;
END PROCESS t_prcs_dIn_4;
-- dIn[3]
t_prcs_dIn_3: PROCESS
BEGIN
	dIn(3) <= '0';
WAIT;
END PROCESS t_prcs_dIn_3;
-- dIn[2]
t_prcs_dIn_2: PROCESS
BEGIN
	dIn(2) <= '1';
WAIT;
END PROCESS t_prcs_dIn_2;
-- dIn[1]
t_prcs_dIn_1: PROCESS
BEGIN
	dIn(1) <= '1';
WAIT;
END PROCESS t_prcs_dIn_1;
-- dIn[0]
t_prcs_dIn_0: PROCESS
BEGIN
	dIn(0) <= '0';
WAIT;
END PROCESS t_prcs_dIn_0;

-- dir
t_prcs_dir: PROCESS
BEGIN
	FOR i IN 1 TO 12
	LOOP
		dir <= '0';
		WAIT FOR 40000 ps;
		dir <= '1';
		WAIT FOR 40000 ps;
	END LOOP;
	dir <= '0';
WAIT;
END PROCESS t_prcs_dir;
-- sel[2]
t_prcs_sel_2: PROCESS
BEGIN
	sel(2) <= '1';
	WAIT FOR 80000 ps;
	sel(2) <= '0';
	WAIT FOR 640000 ps;
	sel(2) <= '1';
	WAIT FOR 160000 ps;
	sel(2) <= '0';
	WAIT FOR 80000 ps;
	sel(2) <= '1';
WAIT;
END PROCESS t_prcs_sel_2;
-- sel[1]
t_prcs_sel_1: PROCESS
BEGIN
	sel(1) <= '1';
	WAIT FOR 80000 ps;
	sel(1) <= '0';
	WAIT FOR 80000 ps;
	sel(1) <= '1';
	WAIT FOR 160000 ps;
	sel(1) <= '0';
	WAIT FOR 160000 ps;
	sel(1) <= '1';
	WAIT FOR 240000 ps;
	sel(1) <= '0';
	WAIT FOR 80000 ps;
	sel(1) <= '1';
	WAIT FOR 80000 ps;
	sel(1) <= '0';
WAIT;
END PROCESS t_prcs_sel_1;
-- sel[0]
t_prcs_sel_0: PROCESS
BEGIN
	sel(0) <= '0';
	WAIT FOR 240000 ps;
	sel(0) <= '1';
	WAIT FOR 80000 ps;
	sel(0) <= '0';
	WAIT FOR 320000 ps;
	sel(0) <= '1';
	WAIT FOR 240000 ps;
	sel(0) <= '0';
	WAIT FOR 80000 ps;
	sel(0) <= '1';
WAIT;
END PROCESS t_prcs_sel_0;
END rot_8bit_arch;
