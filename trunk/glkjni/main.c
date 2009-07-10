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

#include <stdlib.h>
#include <stdio.h>
#include <jni.h>
#include "glk.h"
#include "glkjni.h"
#include "jcall.h"

static JavaVM *vm;

static void create_jvm(char** options, int nOptions)
{
    JavaVMInitArgs args;
    JavaVMOption *vmOptions = NULL;
    jint err;

    if (nOptions) {
        int i;

        vmOptions =
            (JavaVMOption *)gli_malloc(sizeof(JavaVMOption) * nOptions);

        for (i = 0; i < nOptions; i++) {
            vmOptions[i].optionString = options[i];
            vmOptions[i].extraInfo = NULL;
        }
    }

    args.nOptions = nOptions;
    args.ignoreUnrecognized = TRUE;
    args.version = JNI_VERSION_1_4;
    args.options = vmOptions;

    err = JNI_CreateJavaVM(&vm, (void **)&jni_env, &args);
    if (err != JNI_OK) {
        gli_fatal("GlkJNI: unable to launch Java VM\n");
    }

    free(vmOptions);
}

int main(int argc, char **argv)
{
    if (sizeof(glui32) != 4) {
        printf("Compile-time error: glui32 is not a 32-bit value. Please fix glk.h.\n");
        return EXIT_FAILURE;
    }
    if ((glui32)(-1) < 0) {
        printf("Compile-time error: glui32 is not unsigned. Please fix glk.h.\n");
        return EXIT_FAILURE;
    }

    create_jvm(argv + 1, argc - 1);
    jni_jcall_init(argc, argv);
    gli_initialize_latin1();

    glk_main();
    glk_exit();

    return EXIT_SUCCESS;
}

glui32 glk_gestalt(glui32 sel, glui32 val)
{
    return glk_gestalt_ext(sel, val, NULL, 0);
}

glui32 glk_gestalt_ext(glui32 sel, glui32 val, glui32 *arr, glui32 arrlen)
{
    jint res;
    jintArray ints = NULL;

    if (arr && arrlen) {
        if ((jint)arrlen < 0) {
            gli_strict_warning("glk_gestalt_ext: arrlen too large");
            return FALSE;
        }
        ints = (*jni_env)->NewIntArray(jni_env, arrlen);
        if (!ints) {
            jni_no_mem();
        }
    }

    res = (*jni_env)->CallIntMethod(GLK_M(GESTALT),
            (jint)sel, (jint)val, ints);
    jni_exit_on_exc();

    if (arr && arrlen) {
        (*jni_env)->GetIntArrayRegion(jni_env, ints, 0, arrlen,
                (jint *)arr);
        (*jni_env)->DeleteLocalRef(jni_env, ints);
    }

    return (glui32)res;
}

void glk_exit()
{
    gli_windows_print();
    (*jni_env)->CallVoidMethod(GLK_M(EXIT));
    jni_exit_on_exc();
    (*vm)->DestroyJavaVM(vm);
    exit(EXIT_SUCCESS);
}

void glk_tick()
{
    /* Do nothing. */
}

void glk_set_interrupt_handler(void (*func)())
{
    /* Do nothing. */
}
