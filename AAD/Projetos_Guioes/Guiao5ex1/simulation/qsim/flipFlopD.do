onerror {exit -code 1}
vlib work
vcom -work work flipFlopD.vho
vcom -work work flipFlopD.vwf.vht
vsim -c -t 1ps -L cycloneiv -L altera -L altera_mf -L 220model -L sgate -L altera_lnsim work.flipFlopD_vhd_vec_tst
vcd file -direction flipFlopD.msim.vcd
vcd add -internal flipFlopD_vhd_vec_tst/*
vcd add -internal flipFlopD_vhd_vec_tst/i1/*
proc simTimestamp {} {
    echo "Simulation time: $::now ps"
    if { [string equal running [runStatus]] } {
        after 2500 simTimestamp
    }
}
after 2500 simTimestamp
run -all
quit -f



