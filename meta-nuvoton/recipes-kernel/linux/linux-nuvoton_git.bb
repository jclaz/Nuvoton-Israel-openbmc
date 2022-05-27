
#KBRANCH ?= "dev-5.14"
#LINUX_VERSION ?= "5.14"
#SRCREV="e2413239f9a751a2a7491569d27dce76773f2777"

KBRANCH ?= "NPCM-5.10-OpenBMC"
LINUX_VERSION ?= "5.10.67"
SRCREV = "9494916995b9c8082a29289a61a9a5ce0b9d2bd7"

require linux-nuvoton.inc

SRC_URI:append:nuvoton = " file://0003-i2c-nuvoton-npcm750-runbmc-integrate-the-slave-mqueu.patch"
SRC_URI:append:nuvoton = " file://0004-driver-ncsi-replace-del-timer-sync.patch"
SRC_URI:append:nuvoton = " file://0015-driver-misc-nuvoton-vdm-support-openbmc-libmctp.patch"
SRC_URI:append:nuvoton = " file://0017-drivers-i2c-workaround-for-i2c-slave-behavior.patch"

# V4L2 VCD driver
# SRC_URI:append:nuvoton = " file://v4l2.cfg"

# New Arch VDMX/VDMA driver
# SRC_URI:append:nuvoton = " file://2222-driver-misc-add-nuvoton-vdmx-vdma-driver.patch"
