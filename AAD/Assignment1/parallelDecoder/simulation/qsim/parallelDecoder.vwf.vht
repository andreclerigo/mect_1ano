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
-- Generated on "11/13/2022 21:44:42"
                                                             
-- Vhdl Test Bench(with test vectors) for design  :          parallelDecoder
-- 
-- Simulation tool : 3rd Party
-- 

LIBRARY ieee;                                               
USE ieee.std_logic_1164.all;                                

ENTITY parallelDecoder_vhd_vec_tst IS
END parallelDecoder_vhd_vec_tst;
ARCHITECTURE parallelDecoder_arch OF parallelDecoder_vhd_vec_tst IS
-- constants                                                 
-- signals                                                   
SIGNAL M : STD_LOGIC_VECTOR(3 DOWNTO 0);
SIGNAL V : STD_LOGIC;
SIGNAL Y : STD_LOGIC_VECTOR(7 DOWNTO 0);
COMPONENT parallelDecoder
	PORT (
	M : BUFFER STD_LOGIC_VECTOR(3 DOWNTO 0);
	V : BUFFER STD_LOGIC;
	Y : IN STD_LOGIC_VECTOR(7 DOWNTO 0)
	);
END COMPONENT;
BEGIN
	i1 : parallelDecoder
	PORT MAP (
-- list connections between master ports and signals
	M => M,
	V => V,
	Y => Y
	);
-- Y[7]
t_prcs_Y_7: PROCESS
BEGIN
	Y(7) <= '0';
	WAIT FOR 20000 ps;
	Y(7) <= '1';
	WAIT FOR 40000 ps;
	Y(7) <= '0';
	WAIT FOR 20000 ps;
	Y(7) <= '1';
	WAIT FOR 20000 ps;
	Y(7) <= '0';
	WAIT FOR 40000 ps;
	Y(7) <= '1';
	WAIT FOR 40000 ps;
	Y(7) <= '0';
	WAIT FOR 40000 ps;
	Y(7) <= '1';
	WAIT FOR 20000 ps;
	Y(7) <= '0';
	WAIT FOR 20000 ps;
	Y(7) <= '1';
	WAIT FOR 40000 ps;
	Y(7) <= '0';
	WAIT FOR 60000 ps;
	Y(7) <= '1';
	WAIT FOR 20000 ps;
	Y(7) <= '0';
	WAIT FOR 40000 ps;
	Y(7) <= '1';
	WAIT FOR 20000 ps;
	Y(7) <= '0';
	WAIT FOR 20000 ps;
	Y(7) <= '1';
	WAIT FOR 20000 ps;
	Y(7) <= '0';
	WAIT FOR 20000 ps;
	Y(7) <= '1';
WAIT;
END PROCESS t_prcs_Y_7;
-- Y[6]
t_prcs_Y_6: PROCESS
BEGIN
	Y(6) <= '0';
	WAIT FOR 40000 ps;
	Y(6) <= '1';
	WAIT FOR 80000 ps;
	Y(6) <= '0';
	WAIT FOR 40000 ps;
	Y(6) <= '1';
	WAIT FOR 40000 ps;
	Y(6) <= '0';
	WAIT FOR 80000 ps;
	Y(6) <= '1';
	WAIT FOR 40000 ps;
	Y(6) <= '0';
	WAIT FOR 20000 ps;
	Y(6) <= '1';
	WAIT FOR 20000 ps;
	Y(6) <= '0';
	WAIT FOR 40000 ps;
	Y(6) <= '1';
	WAIT FOR 20000 ps;
	Y(6) <= '0';
	WAIT FOR 20000 ps;
	Y(6) <= '1';
	WAIT FOR 20000 ps;
	Y(6) <= '0';
WAIT;
END PROCESS t_prcs_Y_6;
-- Y[5]
t_prcs_Y_5: PROCESS
BEGIN
	Y(5) <= '0';
	WAIT FOR 20000 ps;
	Y(5) <= '1';
	WAIT FOR 20000 ps;
	Y(5) <= '0';
	WAIT FOR 20000 ps;
	Y(5) <= '1';
	WAIT FOR 40000 ps;
	Y(5) <= '0';
	WAIT FOR 20000 ps;
	Y(5) <= '1';
	WAIT FOR 20000 ps;
	Y(5) <= '0';
	WAIT FOR 20000 ps;
	Y(5) <= '1';
	WAIT FOR 20000 ps;
	Y(5) <= '0';
	WAIT FOR 20000 ps;
	Y(5) <= '1';
	WAIT FOR 20000 ps;
	Y(5) <= '0';
	WAIT FOR 40000 ps;
	Y(5) <= '1';
	WAIT FOR 20000 ps;
	Y(5) <= '0';
	WAIT FOR 20000 ps;
	Y(5) <= '1';
	WAIT FOR 20000 ps;
	Y(5) <= '0';
	WAIT FOR 80000 ps;
	Y(5) <= '1';
	WAIT FOR 20000 ps;
	Y(5) <= '0';
	WAIT FOR 20000 ps;
	Y(5) <= '1';
	WAIT FOR 60000 ps;
	Y(5) <= '0';
WAIT;
END PROCESS t_prcs_Y_5;
-- Y[4]
t_prcs_Y_4: PROCESS
BEGIN
	Y(4) <= '0';
	WAIT FOR 80000 ps;
	Y(4) <= '1';
	WAIT FOR 160000 ps;
	Y(4) <= '0';
	WAIT FOR 160000 ps;
	Y(4) <= '1';
	WAIT FOR 20000 ps;
	Y(4) <= '0';
	WAIT FOR 20000 ps;
	Y(4) <= '1';
	WAIT FOR 20000 ps;
	Y(4) <= '0';
WAIT;
END PROCESS t_prcs_Y_4;
-- Y[3]
t_prcs_Y_3: PROCESS
BEGIN
	Y(3) <= '0';
	WAIT FOR 20000 ps;
	Y(3) <= '1';
	WAIT FOR 40000 ps;
	Y(3) <= '0';
	WAIT FOR 40000 ps;
	Y(3) <= '1';
	WAIT FOR 40000 ps;
	Y(3) <= '0';
	WAIT FOR 20000 ps;
	Y(3) <= '1';
	WAIT FOR 20000 ps;
	Y(3) <= '0';
	WAIT FOR 40000 ps;
	Y(3) <= '1';
	WAIT FOR 40000 ps;
	Y(3) <= '0';
	WAIT FOR 40000 ps;
	Y(3) <= '1';
	WAIT FOR 60000 ps;
	Y(3) <= '0';
	WAIT FOR 20000 ps;
	Y(3) <= '1';
	WAIT FOR 20000 ps;
	Y(3) <= '0';
	WAIT FOR 20000 ps;
	Y(3) <= '1';
	WAIT FOR 80000 ps;
	Y(3) <= '0';
WAIT;
END PROCESS t_prcs_Y_3;
-- Y[2]
t_prcs_Y_2: PROCESS
BEGIN
	Y(2) <= '0';
	WAIT FOR 40000 ps;
	Y(2) <= '1';
	WAIT FOR 40000 ps;
	Y(2) <= '0';
	WAIT FOR 40000 ps;
	Y(2) <= '1';
	WAIT FOR 80000 ps;
	Y(2) <= '0';
	WAIT FOR 40000 ps;
	Y(2) <= '1';
	WAIT FOR 40000 ps;
	Y(2) <= '0';
	WAIT FOR 40000 ps;
	Y(2) <= '1';
	WAIT FOR 40000 ps;
	Y(2) <= '0';
	WAIT FOR 20000 ps;
	Y(2) <= '1';
	WAIT FOR 40000 ps;
	Y(2) <= '0';
	WAIT FOR 60000 ps;
	Y(2) <= '1';
	WAIT FOR 20000 ps;
	Y(2) <= '0';
WAIT;
END PROCESS t_prcs_Y_2;
-- Y[1]
t_prcs_Y_1: PROCESS
BEGIN
	Y(1) <= '0';
	WAIT FOR 20000 ps;
	Y(1) <= '1';
	WAIT FOR 20000 ps;
	Y(1) <= '0';
	WAIT FOR 20000 ps;
	Y(1) <= '1';
	WAIT FOR 20000 ps;
	Y(1) <= '0';
	WAIT FOR 20000 ps;
	Y(1) <= '1';
	WAIT FOR 20000 ps;
	Y(1) <= '0';
	WAIT FOR 20000 ps;
	Y(1) <= '1';
	WAIT FOR 40000 ps;
	Y(1) <= '0';
	WAIT FOR 20000 ps;
	Y(1) <= '1';
	WAIT FOR 20000 ps;
	Y(1) <= '0';
	WAIT FOR 20000 ps;
	Y(1) <= '1';
	WAIT FOR 20000 ps;
	Y(1) <= '0';
	WAIT FOR 20000 ps;
	Y(1) <= '1';
	WAIT FOR 20000 ps;
	Y(1) <= '0';
	WAIT FOR 100000 ps;
	Y(1) <= '1';
	WAIT FOR 20000 ps;
	Y(1) <= '0';
	WAIT FOR 20000 ps;
	Y(1) <= '1';
	WAIT FOR 40000 ps;
	Y(1) <= '0';
WAIT;
END PROCESS t_prcs_Y_1;
-- Y[0]
t_prcs_Y_0: PROCESS
BEGIN
	Y(0) <= '0';
	WAIT FOR 160000 ps;
	Y(0) <= '1';
	WAIT FOR 220000 ps;
	Y(0) <= '0';
	WAIT FOR 20000 ps;
	Y(0) <= '1';
	WAIT FOR 20000 ps;
	Y(0) <= '0';
	WAIT FOR 20000 ps;
	Y(0) <= '1';
	WAIT FOR 40000 ps;
	Y(0) <= '0';
WAIT;
END PROCESS t_prcs_Y_0;
END parallelDecoder_arch;
