/* This file is a part of GlkJNI.
 * Copyright (c) 2009 Edward McCardell
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <assert.h>
#include <string.h>
#include <jni.h>
#include "glk.h"
#include "glkjni.h"
#include "glkstart.h"
#include "jcall.h"

JNIEnv *jni_env;
jobject glkobj;

static glkunix_startup_t *startdata;

classcache_t jni_ccache[] = {
        { GLK_CLASS, "glkjni/Glk" },
        { GLKFACTORY_CLASS, "glkjni/GlkFactory" },
        { GLKWINDOW_CLASS, "glkjni/GlkWindow" },
        { GLKCHANNEL_CLASS, "glkjni/GlkSChannel" },
        { ERROR_CLASS, "java/lang/Error" },
        { UOE_CLASS, "java/lang/UnsupportedOperationException" },
        { STRING_CLASS, "java/lang/String" },
        { FILE_CLASS, "java/io/File" },
        { BUFFER_CLASS, "java/nio/Buffer"},
        { BYTEBUFFER_CLASS, "java/nio/ByteBuffer" }
};

#define METHOD(c, m, name, sig) \
{ c ## _CLASS, c ## _ ## m ## _METHOD, name, sig }

methodcache_t jni_mcache[] = {

        /* Static methods. */

        METHOD(GLKFACTORY, STARTUP, "newInstance",
                "([Ljava/lang/String;)V"),
        METHOD(FILE, CREATETEMP, "createTempFile",
                "(Ljava/lang/String;Ljava/lang/String;)Ljava/io/File;"),

        /* Instance methods. */

        METHOD(GLK, EXIT, "exit", "()V"),
        METHOD(GLK, GESTALT, "gestalt", "(II[I)I"),
        METHOD(GLK, WINDOWOPEN, "windowOpen",
                "(Lglkjni/GlkWindow;IIII[Lglkjni/GlkWindow;)V"),
        METHOD(GLK, WINDOWCLOSE, "windowClose", "(Lglkjni/GlkWindow;)V"),
        METHOD(GLK, SETHINT, "setStyleHint", "(IIII)V"),
        METHOD(GLK, CLEARHINT, "clearStyleHint", "(III)V"),
        METHOD(GLK, NAMEDFILE, "namedFile",
                "(Ljava/lang/String;I)Ljava/io/File;"),
        METHOD(GLK, PROMPTFILE, "promptFile", "(II)Ljava/io/File;"),
        METHOD(GLK, REQUESTTIMER, "requestTimer", "(I)V"),
        METHOD(GLK, CANCELTIMER, "cancelTimer", "()V"),
        METHOD(GLK, IMAGEINFO, "getImageInfo", "(I[I)Z"),
        METHOD(GLK, CREATECHAN, "createChannel",
                "()Lglkjni/GlkSChannel;"),
        METHOD(GLK, CHANNELDESTROY, "destroyChannel",
                "(Lglkjni/GlkSChannel;)V"),
        METHOD(GLK, SOUNDHINT, "setSoundLoadHint", "(IZ)V"),
        METHOD(GLK, SELECT, "select", "([I)V"),
        METHOD(GLK, POLL, "poll", "([I)V"),

        METHOD(GLKWINDOW, PRINT, "print", "(Ljava/lang/String;)V"),
        METHOD(GLKWINDOW, STYLE, "setStyle", "(I)V"),
        METHOD(GLKWINDOW, DISTINGUISH, "distinguishStyles", "(II)Z"),
        METHOD(GLKWINDOW, MEASURESTYLE, "measureStyle", "(II)I"),
        METHOD(GLKWINDOW, CLEAR, "clear", "()V"),
        METHOD(GLKWINDOW, CURSOR, "moveCursor", "(II)V"),
        METHOD(GLKWINDOW, SIZE, "getSize", "([I)V"),
        METHOD(GLKWINDOW, ARRANGE, "setArrangement",
                "(IILglkjni/GlkWindow;)V"),
        METHOD(GLKWINDOW, REQUESTCHAR, "requestCharEvent", "(Z)V"),
        METHOD(GLKWINDOW, CANCELCHAR, "cancelCharEvent", "()V"),
        METHOD(GLKWINDOW, REQUESTLINE, "requestLineEvent",
                "(Ljava/nio/ByteBuffer;II)V"),
        METHOD(GLKWINDOW, REQUESTLINEUNI, "requestLineEventUni",
                "(Ljava/nio/IntBuffer;II)V"),
        METHOD(GLKWINDOW, CANCELLINE, "cancelLineEvent", "()I"),
        METHOD(GLKWINDOW, REQUESTMOUSE, "requestMouseEvent", "()V"),
        METHOD(GLKWINDOW, CANCELMOUSE, "cancelMouseEvent", "()V"),
        METHOD(GLKWINDOW, SETLINK, "setLinkValue", "(I)V"),
        METHOD(GLKWINDOW, REQUESTLINK, "requestLinkEvent", "()V"),
        METHOD(GLKWINDOW, CANCELLINK, "cancelLinkEvent", "()V"),
        METHOD(GLKWINDOW, DRAWINLINE, "drawInlineImage", "(II)Z"),
        METHOD(GLKWINDOW, DRAWINLINESCALED, "drawInlineImage", "(IIII)Z"),
        METHOD(GLKWINDOW, FLOWBREAK, "flowBreak", "()V"),
        METHOD(GLKWINDOW, DRAW, "drawImage", "(III)Z"),
        METHOD(GLKWINDOW, DRAWSCALED, "drawImage", "(IIIII)Z"),
        METHOD(GLKWINDOW, SETBG, "setBackgroundColor", "(I)V"),
        METHOD(GLKWINDOW, ERASERECT, "eraseRect", "(IIII)V"),
        METHOD(GLKWINDOW, FILLRECT, "fillRect", "(IIIII)V"),

        METHOD(GLKCHANNEL, VOLUME, "setVolume", "(I)V"),
        METHOD(GLKCHANNEL, PLAY, "play", "(IIZ)Z"),
        METHOD(GLKCHANNEL, STOP, "stop", "()V"),

        METHOD(STRING, FROMNATIVE, "<init>", "([B)V"),
        METHOD(STRING, FROMLATIN1, "<init>", "([BI)V"),
        METHOD(STRING, GETBYTES, "getBytes", "()[B"),
        METHOD(FILE, FROMSTRING, "<init>", "(Ljava/lang/String;)V"),
        METHOD(FILE, GETPATH, "getPath", "()Ljava/lang/String;"),
        METHOD(FILE, GETABSPATH, "getAbsolutePath",
                "()Ljava/lang/String;"),
        METHOD(FILE, DELETEONEXIT, "deleteOnExit", "()V"),
        METHOD(FILE, DELETE, "delete", "()Z"),
        METHOD(FILE, EXISTS, "exists", "()Z"),
        METHOD(BYTEBUFFER, ASINTBUF, "asIntBuffer",
                "()Ljava/nio/IntBuffer;")
};

void jni_no_mem()
{
    fputs("GlkJNI: the JVM was unable to allocate sufficient memory\n",
            stderr);
    exit(EXIT_FAILURE);
}

/*
 * Creates a new global reference from a local reference, and deletes
 * the local reference. Never returns NULL.
 */
jobject jni_new_global(jobject localref)
{
    jobject globalref = (*jni_env)->NewGlobalRef(jni_env, localref);
    (*jni_env)->DeleteLocalRef(jni_env, localref);
    if (!globalref) {
        jni_no_mem();
    }

    return globalref;
}

/*
 * Exits the program if a Java exception has occurred.
 */
void jni_exit_on_exc()
{
    if ((*jni_env)->ExceptionCheck(jni_env)) {
        (*jni_env)->ExceptionDescribe(jni_env);
        exit(EXIT_FAILURE);
    }
}

/*
 * Returns TRUE if a (non-error) exception has occurred, FALSE otherwise.
 * Exits the program if a Java error has occurred.
 */
int jni_check_exc()
{
   jthrowable exc = (*jni_env)->ExceptionOccurred(jni_env);
   if (!exc) {
       return FALSE;
   }
   if (INSTANCE_OF(exc, ERROR_CLASS)) {
       (*jni_env)->ExceptionDescribe(jni_env);
       exit(EXIT_FAILURE);
   }
   (*jni_env)->ExceptionClear(jni_env);
   (*jni_env)->DeleteLocalRef(jni_env, exc);
   return TRUE;
}

/*
 * Returns TRUE if an exception occurred and it is an instance of
 * CLASS_ID, FALSE if no exception occurred, and exits the program
 * otherwise.
 */
int jni_check_for_exc(int class_id)
{
   jthrowable exc = (*jni_env)->ExceptionOccurred(jni_env);
   if (!exc) {
       return FALSE;
   }
   if (INSTANCE_OF(exc, ERROR_CLASS)) {
       (*jni_env)->ExceptionDescribe(jni_env);
       exit(EXIT_FAILURE);
   } else if (!INSTANCE_OF(exc, class_id)) {
       (*jni_env)->ExceptionDescribe(jni_env);
       exit(EXIT_FAILURE);
   }
   (*jni_env)->ExceptionClear(jni_env);
   (*jni_env)->DeleteLocalRef(jni_env, exc);
   return TRUE;
}

jobject jni_newbytebuffer(void *buf, jlong len)
{
    jobject bytebuf;

    bytebuf = (*jni_env)->NewDirectByteBuffer(jni_env, buf, len);
    jni_exit_on_exc();
    if (!bytebuf) {
        gli_fatal("JNI error: could not create ByteBuffer");
    }
    return bytebuf;
}

/*
 * Creates a jbyte array from a C string. The return value is a local
 * reference.
 */
jbyteArray jni_bytesfromstr(char *str)
{
    jsize len = (jsize)strlen(str);
    jbyteArray bytes;

    bytes = (*jni_env)->NewByteArray(jni_env, len);
    if (!bytes) {
        jni_no_mem();
    }
    (*jni_env)->SetByteArrayRegion(jni_env, bytes, 0, len, (jbyte *)str);

    return bytes;
}

/*
 * Creates a jstring from a C string (which is assumed to be in the
 * platform's native encoding). The return value is a local reference.
 */
jstring jni_jstrfromnative(char *str)
{
    jstring jstr;
    jbyteArray bytes;

    bytes = jni_bytesfromstr(str);
    jstr = (*jni_env)->NewObject(STATIC_M(STRING, FROMNATIVE), bytes, 0);
    (*jni_env)->DeleteLocalRef(jni_env, bytes);
    if (jni_check_exc()) {
        return NULL;
    }

    return jstr;
}

/*
 * Creates a C string in the platform's native encoding from a jstring.
 * The string is allocated on the heap.
 */
char *jni_nativefromjstr(jstring jstr)
{
    jbyteArray bytes = NULL;
    char *str = NULL;
    jsize len;

    bytes = (*jni_env)->CallObjectMethod(
            INSTANCE_M(jstr, STRING_GETBYTES));
    if (jni_check_exc()) {
        return NULL;
    }

    len = (*jni_env)->GetArrayLength(jni_env, bytes);
    str = (char *)gli_malloc(len + 1);
    (*jni_env)->GetByteArrayRegion(jni_env, bytes, 0, len, (jbyte *)str);
    (*jni_env)->DeleteLocalRef(jni_env, bytes);
    str[len] = 0;

    return str;
}

char *jni_file_getpath(jobject file)
{
    jstring jpath;
    char *path = NULL;

    jpath = (*jni_env)->CallObjectMethod(INSTANCE_M(file, FILE_GETPATH));
    if (jni_check_exc()) {
        goto done;
    }

    path = jni_nativefromjstr(jpath);

done:
    (*jni_env)->DeleteLocalRef(jni_env, jpath);
    return path;
}

static void no_class_def(char *name)
{
    fprintf(stderr, "GlkJNI: Unable to locate class %s\n", name);
    exit(EXIT_FAILURE);
}

static void jni_init_classes()
{
    int i;
    jobject localref;

    for (i = 0; i < MAX_CLASS_ID; i++) {
        assert(jni_ccache[i].id == i);

        localref = (*jni_env)->FindClass(jni_env, jni_ccache[i].name);
        if (!localref) {
            no_class_def(jni_ccache[i].name);
        }

        jni_ccache[i].class = jni_new_global(localref);
    }
}

static void no_such_method(char *class, char *method)
{
    fprintf(stderr, "GlkJNI: unable to locate method %s.%s\n",
            class, method);
    exit(EXIT_FAILURE);
}

static void jni_init_methods()
{
    int i;
    jmethodID mid;

    for (i = 0; i < MAX_IMETHOD_ID; i++) {
        int class_id;
        jclass class;

        assert(jni_mcache[i].id == i);

        class_id = jni_mcache[i].class_id;
        class = jni_ccache[class_id].class;

        if (i < MAX_SMETHOD_ID) {
            mid = (*jni_env)->GetStaticMethodID(jni_env, class,
                    jni_mcache[i].name, jni_mcache[i].sig);
        } else {
            mid = (*jni_env)->GetMethodID(jni_env, class,
                    jni_mcache[i].name, jni_mcache[i].sig);
        }
        if (!mid) {
            no_such_method(jni_ccache[class_id].name, jni_mcache[i].name);
        }

        jni_mcache[i].mid = mid;
    }
}

/*
 * Creates an array of jstrings from the command-line arguments.
 * Arguments understood by the VM (-verbose, -Dxxx=yyy, -Xxxx) are
 * removed. The return value is a local reference.
 */
static jobjectArray jni_argv(int argc, char **argv)
{
    jobjectArray arr = NULL;
    jstring jstr;
    int jArgc = 0;
    char **jArgv;
    int i;

    jArgv = (char **)gli_malloc((1 + argc) * sizeof(char **));
    for (i = 0; i < argc; i++) {
        if (i == 0) {
            /* Don't bother examining program name. */
        } else if (!strncmp("-X", argv[i], 2)) {
            if (argv[i][2] != '\0') {
                continue;
            }
        } else if (!strncmp("-D", argv[i], 2)) {
            if (strchr(argv[i], '=')) {
                continue;
            }
        } else if (!strncmp("-verbose", argv[i], 8)) {
            continue;
        }
        jArgv[jArgc++] = argv[i];
    }
    jArgv[jArgc] = NULL;

    arr = (*jni_env)->NewObjectArray(jni_env, jArgc, JNI_CLASS(STRING),
            NULL);
    if (!arr) {
        jni_no_mem();
    }

    for (i = 0; i < jArgc; i++) {
        jstr = jni_jstrfromnative(jArgv[i]);
        if (jstr) {
            (*jni_env)->SetObjectArrayElement(jni_env, arr, i, jstr);
            (*jni_env)->DeleteLocalRef(jni_env, jstr);
        }
    }

    return arr;
}

jboolean JNICALL jni_glkstartup(JNIEnv *env, jclass class,
        jobject gobj, jobjectArray args)
{
    jsize jArgc;
    jsize argc = 0;
    char **argv;
    int i;

    if (startdata) {
        goto whoops;
    }
    if (!gobj) {
        goto whoops;
    }
    if (!args) {
        goto whoops;
    }
    jArgc = (*jni_env)->GetArrayLength(jni_env, args);
    if (jArgc < 1) {
        goto whoops;
    }

    glkobj = jni_new_global(gobj);

    argv = (char **)gli_malloc((1 + jArgc) * sizeof(char **));

    for (i = 0; i < jArgc; i++) {
        char *arg;
        jstring jArg = (*jni_env)->GetObjectArrayElement(jni_env, args, i);
        if (!jArg) {
            continue;
        }
        arg = jni_nativefromjstr(jArg);
        if (arg[0] == '\0') {
            continue;
        }
        argv[argc++] = arg;
    }
    argv[argc] = NULL;

    startdata = (glkunix_startup_t *)gli_malloc(sizeof(glkunix_startup_t));
    startdata->argc = argc;
    startdata->argv = argv;

    return glkunix_startup_code(startdata);

whoops:
    gli_fatal("GlkJNI error: startup called with invalid args\n");

    /* Should never get here. */
    return FALSE;
}

static void jni_register_natives()
{
    JNINativeMethod nm;

    nm.name = "startup";
    nm.signature = "(Lglkjni/Glk;[Ljava/lang/String;)Z";
    nm.fnPtr = jni_glkstartup;

    (*jni_env)->RegisterNatives(jni_env,
            jni_ccache[GLKFACTORY_CLASS].class, &nm, 1);
    jni_exit_on_exc();
}

static void jni_init_glk(int argc, char **argv)
{
    jobjectArray jArgv;

    jArgv = jni_argv(argc, argv);

    (*jni_env)->CallStaticVoidMethod(
            STATIC_M(GLKFACTORY, STARTUP), jArgv);
    (*jni_env)->DeleteLocalRef(jni_env, jArgv);
    jni_exit_on_exc();

    if (!glkobj) {
        gli_fatal("GlkJNI: startup code not run");
    }
}

void jni_jcall_init(int argc, char **argv)
{
    jni_init_classes();
    jni_init_methods();
    jni_register_natives();
    jni_init_glk(argc, argv);
}
