all:
	@(if [ ! -d build ]; then mkdir build; fi; cd build;\
	  if [ ! -d opencv-bin ]; then mkdir video-bin; fi; cmake ..; make)

clean:
	rm -rf build ../opencv-bin
