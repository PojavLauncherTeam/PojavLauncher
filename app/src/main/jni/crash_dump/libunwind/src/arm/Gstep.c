/* libunwind - a platform-independent unwind library
   Copyright (C) 2008 CodeSourcery
   Copyright 2011 Linaro Limited
   Copyright (C) 2012 Tommi Rantala <tt.rantala@gmail.com>

This file is part of libunwind.

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.  */

#include "unwind_i.h"
#include "offsets.h"
#include "ex_tables.h"

#include <signal.h>

#define arm_exidx_step	UNW_OBJ(arm_exidx_step)

static inline int
arm_exidx_step (struct cursor *c)
{
  uint8_t buf[32];
  int ret;

  /* mark PC unsaved */
  c->dwarf.loc[UNW_ARM_R15] = DWARF_NULL_LOC;

  if ((ret = tdep_find_proc_info (&c->dwarf, c->dwarf.ip, 1)) < 0)
     return ret;

  if (c->dwarf.pi.format != UNW_INFO_FORMAT_ARM_EXIDX)
    return -UNW_ENOINFO;

  ret = arm_exidx_extract (&c->dwarf, buf);
  if (ret == -UNW_ESTOPUNWIND)
    return 0;
  else if (ret < 0)
    return ret;

  ret = arm_exidx_decode (buf, ret, &c->dwarf);
  if (ret < 0)
    return ret;

  c->dwarf.pi_valid = 0;

  return (c->dwarf.ip == 0) ? 0 : 1;
}

/* ANDROID support update. */

/* When taking a step back up the stack, the pc will point to the next
 * instruction to execute, not the currently executing instruction. This
 * function adjusts the pc to the currently executing instruction.
 */
static void adjust_ip(struct cursor *c)
{
  unw_word_t ip, value;
  ip = c->dwarf.ip;

  if (ip)
    {
      int adjust = 4;
      if (ip & 1)
        {
          /* Thumb instructions, the currently executing instruction could be
          * 2 or 4 bytes, so adjust appropriately.
          */
          unw_addr_space_t as;
          unw_accessors_t *a;
          void *arg;

          as = c->dwarf.as;
          a = unw_get_accessors (as);
          arg = c->dwarf.as_arg;

          if (ip < 5 || (*a->access_mem) (as, ip-5, &value, 0, arg) < 0 ||
              (value & 0xe000f000) != 0xe000f000)
            adjust = 2;
        }
      c->dwarf.ip -= adjust;
    }
}
/* End of ANDROID update. */

PROTECTED int
unw_handle_signal_frame (unw_cursor_t *cursor)
{
  struct cursor *c = (struct cursor *) cursor;
  int ret;
  unw_word_t sc_addr, sp, sp_addr = c->dwarf.cfa;
  struct dwarf_loc sp_loc = DWARF_LOC (sp_addr, 0);

  if ((ret = dwarf_get (&c->dwarf, sp_loc, &sp)) < 0)
    return -UNW_EUNSPEC;

  /* Obtain signal frame type (non-RT or RT). */
  ret = unw_is_signal_frame (cursor);

  /* Save the SP and PC to be able to return execution at this point
     later in time (unw_resume).  */
  c->sigcontext_sp = c->dwarf.cfa;
  c->sigcontext_pc = c->dwarf.ip;

  /* Since kernel version 2.6.18 the non-RT signal frame starts with a
     ucontext while the RT signal frame starts with a siginfo, followed
     by a sigframe whose first element is an ucontext.
     Prior 2.6.18 the non-RT signal frame starts with a sigcontext while
     the RT signal frame starts with two pointers followed by a siginfo
     and an ucontext. The first pointer points to the start of the siginfo
     structure and the second one to the ucontext structure.  */

  if (ret == 1)
    {
      /* Handle non-RT signal frames. Check if the first word on the stack
	 is the magic number.  */
      if (sp == 0x5ac3c35a)
	{
	  c->sigcontext_format = ARM_SCF_LINUX_SIGFRAME;
	  sc_addr = sp_addr + LINUX_UC_MCONTEXT_OFF;
	}
      else
	{
	  c->sigcontext_format = ARM_SCF_LINUX_OLD_SIGFRAME;
	  sc_addr = sp_addr;
	}
    }
  else if (ret == 2)
    {
      /* Handle RT signal frames. Check if the first word on the stack is a
	 pointer to the siginfo structure.  */
      if (sp == sp_addr + 8)
	{
	  c->sigcontext_format = ARM_SCF_LINUX_OLD_RT_SIGFRAME;
	  sc_addr = sp_addr + 8 + sizeof (siginfo_t) + LINUX_UC_MCONTEXT_OFF;
	}
      else
	{
	  c->sigcontext_format = ARM_SCF_LINUX_RT_SIGFRAME;
	  sc_addr = sp_addr + sizeof (siginfo_t) + LINUX_UC_MCONTEXT_OFF;
	}
    }
  else
    return -UNW_EUNSPEC;

  c->sigcontext_addr = sc_addr;

  /* Update the dwarf cursor.
     Set the location of the registers to the corresponding addresses of the
     uc_mcontext / sigcontext structure contents.  */
  c->dwarf.loc[UNW_ARM_R0] = DWARF_LOC (sc_addr + LINUX_SC_R0_OFF, 0);
  c->dwarf.loc[UNW_ARM_R1] = DWARF_LOC (sc_addr + LINUX_SC_R1_OFF, 0);
  c->dwarf.loc[UNW_ARM_R2] = DWARF_LOC (sc_addr + LINUX_SC_R2_OFF, 0);
  c->dwarf.loc[UNW_ARM_R3] = DWARF_LOC (sc_addr + LINUX_SC_R3_OFF, 0);
  c->dwarf.loc[UNW_ARM_R4] = DWARF_LOC (sc_addr + LINUX_SC_R4_OFF, 0);
  c->dwarf.loc[UNW_ARM_R5] = DWARF_LOC (sc_addr + LINUX_SC_R5_OFF, 0);
  c->dwarf.loc[UNW_ARM_R6] = DWARF_LOC (sc_addr + LINUX_SC_R6_OFF, 0);
  c->dwarf.loc[UNW_ARM_R7] = DWARF_LOC (sc_addr + LINUX_SC_R7_OFF, 0);
  c->dwarf.loc[UNW_ARM_R8] = DWARF_LOC (sc_addr + LINUX_SC_R8_OFF, 0);
  c->dwarf.loc[UNW_ARM_R9] = DWARF_LOC (sc_addr + LINUX_SC_R9_OFF, 0);
  c->dwarf.loc[UNW_ARM_R10] = DWARF_LOC (sc_addr + LINUX_SC_R10_OFF, 0);
  c->dwarf.loc[UNW_ARM_R11] = DWARF_LOC (sc_addr + LINUX_SC_FP_OFF, 0);
  c->dwarf.loc[UNW_ARM_R12] = DWARF_LOC (sc_addr + LINUX_SC_IP_OFF, 0);
  c->dwarf.loc[UNW_ARM_R13] = DWARF_LOC (sc_addr + LINUX_SC_SP_OFF, 0);
  c->dwarf.loc[UNW_ARM_R14] = DWARF_LOC (sc_addr + LINUX_SC_LR_OFF, 0);
  c->dwarf.loc[UNW_ARM_R15] = DWARF_LOC (sc_addr + LINUX_SC_PC_OFF, 0);

  /* Set SP/CFA and PC/IP.  */
  dwarf_get (&c->dwarf, c->dwarf.loc[UNW_ARM_R13], &c->dwarf.cfa);
  dwarf_get (&c->dwarf, c->dwarf.loc[UNW_ARM_R15], &c->dwarf.ip);

  c->dwarf.pi_valid = 0;

  return 1;
}

PROTECTED int
unw_step (unw_cursor_t *cursor)
{
  struct cursor *c = (struct cursor *) cursor;
  int ret = -UNW_EUNSPEC;

  Debug (1, "(cursor=%p)\n", c);

  unw_word_t old_ip = c->dwarf.ip;
  unw_word_t old_cfa = c->dwarf.cfa;

  /* Check if this is a signal frame. */
  if (unw_is_signal_frame (cursor))
    {
      ret = unw_handle_signal_frame (cursor);
    }

#ifdef CONFIG_DEBUG_FRAME
  /* First, try DWARF-based unwinding. */
  if (ret < 0 && UNW_TRY_METHOD(UNW_ARM_METHOD_DWARF))
    {
      ret = dwarf_step (&c->dwarf);
      Debug(1, "dwarf_step()=%d\n", ret);

      if (likely (ret > 0))
        ret = 1;
      else if (unlikely (ret == -UNW_ESTOPUNWIND))
        ret = 0;
    }
#endif /* CONFIG_DEBUG_FRAME */

  /* Next, try extbl-based unwinding. */
  if (ret < 0 && UNW_TRY_METHOD (UNW_ARM_METHOD_EXIDX))
    {
      ret = arm_exidx_step (c);
      if (ret > 0)
	ret = 1;
      if (ret == -UNW_ESTOPUNWIND || ret == 0)
	ret = 0;
    }

  /* Fall back on APCS frame parsing.
     Note: This won't work in case the ARM EABI is used. */
  if (unlikely (ret < 0))
    {
      if (UNW_TRY_METHOD(UNW_ARM_METHOD_FRAME))
        {
          ret = UNW_ESUCCESS;
          /* DWARF unwinding failed, try to follow APCS/optimized APCS frame chain */
          unw_word_t instr, i;
          Debug (13, "dwarf_step() failed (ret=%d), trying frame-chain\n", ret);
          dwarf_loc_t ip_loc, fp_loc;
          unw_word_t frame;
          /* Mark all registers unsaved, since we don't know where
             they are saved (if at all), except for the EBP and
             EIP.  */
          if (dwarf_get(&c->dwarf, c->dwarf.loc[UNW_ARM_R11], &frame) < 0)
            {
              return 0;
            }
          for (i = 0; i < DWARF_NUM_PRESERVED_REGS; ++i) {
            c->dwarf.loc[i] = DWARF_NULL_LOC;
          }
          if (frame)
            {
              if (dwarf_get(&c->dwarf, DWARF_LOC(frame, 0), &instr) < 0)
                {
                  return 0;
                }
              instr -= 8;
              if (dwarf_get(&c->dwarf, DWARF_LOC(instr, 0), &instr) < 0)
                {
                  return 0;
                }
              if ((instr & 0xFFFFD800) == 0xE92DD800)
                {
                  /* Standard APCS frame. */
                  ip_loc = DWARF_LOC(frame - 4, 0);
                  fp_loc = DWARF_LOC(frame - 12, 0);
                }
              else
                {
                  /* Codesourcery optimized normal frame. */
                  ip_loc = DWARF_LOC(frame, 0);
                  fp_loc = DWARF_LOC(frame - 4, 0);
                }
              if (dwarf_get(&c->dwarf, ip_loc, &c->dwarf.ip) < 0)
                {
                  return 0;
                }
              c->dwarf.loc[UNW_ARM_R12] = ip_loc;
              c->dwarf.loc[UNW_ARM_R11] = fp_loc;
              c->dwarf.pi_valid = 0;
              Debug(15, "ip=%x\n", c->dwarf.ip);
            }
          else
            {
              ret = -UNW_ENOINFO;
            }
        }
    }

  /* ANDROID support update. */
  if (ret < 0 && UNW_TRY_METHOD(UNW_ARM_METHOD_LR) && c->dwarf.frame == 0)
    {
      /* If this is the first frame, the code may be executing garbage
       * in the middle of nowhere. In this case, try using the lr as
       * the pc.
       */
      unw_word_t lr;
      if (dwarf_get(&c->dwarf, c->dwarf.loc[UNW_ARM_R14], &lr) >= 0)
        {
          if (lr != c->dwarf.ip)
            {
              ret = 1;
              c->dwarf.ip = lr;
            }
        }
    }
  /* End of ANDROID update. */

  if (ret >= 0)
    {
      adjust_ip(c);
      if (c->dwarf.ip == old_ip && c->dwarf.cfa == old_cfa)
        {
          Dprintf ("%s: ip and cfa unchanged; stopping here (ip=0x%lx)\n",
                   __FUNCTION__, (long) c->dwarf.ip);
          return -UNW_EBADFRAME;
        }
      c->dwarf.frame++;
    }
  return ret == -UNW_ENOINFO ? 0 : ret;
}
