all:
	@(if [ ! -d build ]; then mkdir build; fi; cd build;\
	  if [ ! -d sndfile-example-bin ]; then mkdir sndfile-example-bin; fi; cmake ..; make)

clean:
	rm -rf build ../sndfile-example-bin
