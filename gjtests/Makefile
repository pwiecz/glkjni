GLKINCLUDEDIR = ../glkjni
GLKLIBDIR = ../glkjni
GLKMAKEFILE = Make.glkjni

CC = gcc

#OPTIONS = -O
OPTIONS = -g

include $(GLKINCLUDEDIR)/$(GLKMAKEFILE)

CFLAGS = $(OPTIONS) -I$(GLKINCLUDEDIR)
LIBS = -L$(GLKLIBDIR) $(GLKLIB) $(LINKLIBS)

all: model multiwin

model: model.o
	$(CC) $(OPTIONS) -o model model.o $(LIBS)
	
multiwin: multiwin.o
	$(CC) $(OPTIONS) -o multiwin multiwin.o $(LIBS)

clean:
	$(RM) *.o model multiwin
