/* libunwind - a platform-independent unwind library
   Copyright (C) 2002-2004 Hewlett-Packard Co
	Contributed by David Mosberger-Tang <davidm@hpl.hp.com>

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

PROTECTED int
unw_step (unw_cursor_t *cursor)
{
  struct cursor *c = (struct cursor *) cursor;
  int ret, i;

  Debug (1, "(cursor=%p, ip=0x%08x)\n", c, (unsigned) c->dwarf.ip);

  /* ANDROID support update. */
  /* Save the current ip/cfa to prevent looping if the decode yields
     the same ip/cfa as before. */
  unw_word_t old_ip = c->dwarf.ip;
  unw_word_t old_cfa = c->dwarf.cfa;
  /* End of ANDROID update. */

  /* Try DWARF-based unwinding... */
  ret = dwarf_step (&c->dwarf);

#if !defined(UNW_LOCAL_ONLY)
  /* Do not use this method on a local unwind. There is a very high
   * probability this method will try to access unmapped memory, which
   * will crash the process. Since this almost never actually works,
   * it should be okay to skip.
   */
  if (ret < 0)
    {
      /* DWARF failed, let's see if we can follow the frame-chain
	 or skip over the signal trampoline.  */
      struct dwarf_loc ebp_loc, eip_loc;

      /* We could get here because of missing/bad unwind information.
         Validate all addresses before dereferencing. */
      c->validate = 1;

      Debug (13, "dwarf_step() failed (ret=%d), trying frame-chain\n", ret);

      if (unw_is_signal_frame (cursor))
        {
          ret = unw_handle_signal_frame(cursor);
	  if (ret < 0)
	    {
	      Debug (2, "returning 0\n");
	      return 0;
	    }
        }
      else
	{
	  ret = dwarf_get (&c->dwarf, c->dwarf.loc[EBP], &c->dwarf.cfa);
	  if (ret < 0)
	    {
	      Debug (2, "returning %d\n", ret);
	      return ret;
	    }

	  Debug (13, "[EBP=0x%x] = 0x%x\n", DWARF_GET_LOC (c->dwarf.loc[EBP]),
		 c->dwarf.cfa);

	  ebp_loc = DWARF_LOC (c->dwarf.cfa, 0);
	  eip_loc = DWARF_LOC (c->dwarf.cfa + 4, 0);
	  c->dwarf.cfa += 8;

	  /* Mark all registers unsaved, since we don't know where
	     they are saved (if at all), except for the EBP and
	     EIP.  */
	  for (i = 0; i < DWARF_NUM_PRESERVED_REGS; ++i)
	    c->dwarf.loc[i] = DWARF_NULL_LOC;

          c->dwarf.loc[EBP] = ebp_loc;
          c->dwarf.loc[EIP] = eip_loc;
	}
      c->dwarf.ret_addr_column = EIP;

      if (!DWARF_IS_NULL_LOC (c->dwarf.loc[EBP]))
	{
	  ret = dwarf_get (&c->dwarf, c->dwarf.loc[EIP], &c->dwarf.ip);
	  if (ret < 0)
	    {
	      Debug (13, "dwarf_get([EIP=0x%x]) failed\n", DWARF_GET_LOC (c->dwarf.loc[EIP]));
	      Debug (2, "returning %d\n", ret);
	      return ret;
	    }
	  else
	    {
	      Debug (13, "[EIP=0x%x] = 0x%x\n", DWARF_GET_LOC (c->dwarf.loc[EIP]),
		c->dwarf.ip);
	    }
	}
      else
	c->dwarf.ip = 0;
    }
#endif

  /* ANDROID support update. */
  if (ret >= 0)
    {
      if (c->dwarf.ip)
        {
          /* Adjust the pc to the instruction before. */
          c->dwarf.ip--;
        }
      /* If the decode yields the exact same ip/cfa as before, then indicate
         the unwind is complete. */
      if (old_ip == c->dwarf.ip && old_cfa == c->dwarf.cfa)
        {
          Dprintf ("%s: ip and cfa unchanged; stopping here (ip=0x%lx)\n",
                   __FUNCTION__, (long) c->dwarf.ip);
          return -UNW_EBADFRAME;
        }
      c->dwarf.frame++;
    }
  /* End of ANDROID update. */
  if (unlikely (ret <= 0))
    return 0;

  return (c->dwarf.ip == 0) ? 0 : 1;
}
