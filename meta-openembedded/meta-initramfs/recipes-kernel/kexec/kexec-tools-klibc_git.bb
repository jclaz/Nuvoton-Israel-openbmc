# the binaries are statically linked against klibc
SUMMARY = "Kexec tools, statically compiled against klibc"
AUTHOR = "Eric Biederman"
HOMEPAGE = "http://kernel.org/pub/linux/utils/kernel/kexec/"
SECTION = "kernel/userland"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea5bed2f60d357618ca161ad539f7c0a \
                    file://kexec/kexec.c;beginline=1;endline=20;md5=af10f6ae4a8715965e648aa687ad3e09"
PV = "2.0.18+git${SRCPV}"

DEPENDS = "zlib xz"

inherit klibc autotools siteinfo

SRC_URI = "git://git.kernel.org/pub/scm/utils/kernel/kexec/kexec-tools.git"
SRCREV = "5750980cdbbc33ef75bfba6660295b932376ce15"

BUILD_PATCHES = "file://0001-force-static-build.patch \
                 file://0002-Adjust-the-order-of-headers-to-fix-build-for-musl.patch"

KLIBC_PATCHES += " \
            file://0003-kexec-elf-rel-use-our-elf.h.patch \
            file://0004-kexec-elf-exec.c-replace-with-our-err.h.patch \
            file://0005-crashdump-elf.c-work-around-for-sysconf-_SC_NPROCESS.patch \
            file://0006-kexec-syscall.h-work-around-missing-syscall-wrapper.patch \
            file://0007-kexec.c-add-guard-around-ENOTSUP.patch \
            file://0008-kexec.c-replace-mising-BLKGETSIZE64.patch \
            file://0009-vmcore-dmesg.c-work-around-missing-imaxdiv.patch \
            file://0010-fs2dt.c-work-around-missing-getline.patch \
            file://0011-purgatory-Makefile-adapt-to-klcc.patch \
            file://0012-purgatory-string.c-avoid-inclusion-of-string.h.patch \
            file://0013-sha256.h-avoid-inclusion-of-sys-types.h.patch \
            file://0014-add-if_nameindex-from-musl.patch \
            file://0015-vmcore-dmesg-fix-warning.patch \
            file://klibc-reboot.patch \
            file://include_next.patch \
            "

WARNING_FIXES = ""
FROM_OE_CORE = "file://arm_crashdump-fix-buffer-align.patch \
                file://powerpc_change-the-memory-size-limit.patch \
                file://kexec-x32.patch"

SRC_URI += "${BUILD_PATCHES} ${KLIBC_PATCHES} ${WARNING_FIXES} ${FROM_OE_CORE}"

SRC_URI:append:arm = " file://arm_crashdump.patch"

SRC_URI:append:mips = " file://140-mips_disable_devicetree_support.patch"
SRC_URI:append:mipsel = " file://140-mips_disable_devicetree_support.patch"

SRC_URI:append:x86 = " file://x86_sys_io.patch file://x86_basename.patch \
                       file://x86_vfscanf.patch file://x86_kexec_test.patch"

SRC_URI:append:x86-64 = " file://x86_sys_io.patch file://x86_basename.patch \
                          file://x86_vfscanf.patch file://x86_kexec_test.patch"

SRC_URI:append:aarch64 = " file://arm64_kexec-image-header.h-add-missing-le64toh.patch \
                           file://arm64-crashdump-arm64.c-fix-warning.patch \
                           file://arm64_kexec-arm64.c-workaround-for-getrandom-syscall.patch"

SRC_URI:append:powerpc = " file://powerpc-purgatory-Makefile-remove-unknown-flags.patch"

S = "${WORKDIR}/git"

EXTRA_OECONF += "--without-zlib --without-lzma --without-xen"

# fix purgatory/printf.c:2:10: fatal error: limits.h: No such file or directory
# fix include/limits.h:42:10: fatal error: bitsize/limits.h: No such file or directory
CFLAGS += "-O2 -I${STAGING_DIR_HOST}${libdir}/klibc/include -I${S}/purgatory/include \
           -I${STAGING_DIR_HOST}${libdir}/klibc/include/bits${SITEINFO_BITS}"

do_compile:prepend() {
    # Remove the prepackaged config.h from the source tree as it overrides
    # the same file generated by configure and placed in the build tree
    rm -f ${S}/include/config.h

    # Remove the '*.d' file to make sure the recompile is OK
    for dep in `find ${B} -type f -name '*.d'`; do
        dep_no_d="`echo $dep | sed 's#.d$##'`"
        # Remove file.d when there is a file.o
        if [ -f "$dep_no_d.o" ]; then
            rm -f $dep
        fi
    done
}

PACKAGES =+ "kexec-klibc vmcore-dmesg-klibc"

FILES:kexec-klibc = "${sbindir}/kexec"
FILES:vmcore-dmesg-klibc = "${sbindir}/vmcore-dmesg"

INSANE_SKIP:${PN} = "arch"

COMPATIBLE_HOST = '(x86_64.*|i.86.*|arm.*|aarch64.*|powerpc.*|mips.*)-(linux|freebsd.*)'
