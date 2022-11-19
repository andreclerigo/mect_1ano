onerror {exit -code 1}
vlib work
vcom -work work lRot_8bit.vho
vcom -work work lRot_8bit.vwf.vht
vsim -c -t 1ps -L cycloneiv -L altera -L altera_mf -L 220model -L sgate -L altera_lnsim work.lRot_8bit_vhd_vec_tst
vcd file -direction lRot_8bit.msim.vcd
vcd add -internal lRot_8bit_vhd_vec_tst/*
vcd add -internal lRot_8bit_vhd_vec_tst/i1/*
proc simTimestamp {} {
    echo "Simulation time: $::now ps"
    if { [string equal running [runStatus]] } {
        after 2500 simTimestamp
    }
}
after 2500 simTimestamp
run -all
quit -f

