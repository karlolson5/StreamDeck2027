from __future__ import annotations
import tkinter as tk
from tkinter import messagebox
import ipaddress
from typing import Callable


class SettingsDialog(tk.Toplevel):
    """Modal dialog for editing the robot and simulator IP addresses."""

    def __init__(self, parent: tk.Tk, current_robot_ip: str, current_sim_ip: str,
                 on_save: Callable[[str, str], None]):
        super().__init__(parent)
        self.title("Settings")
        self.resizable(False, False)
        self.transient(parent)   # stays on top of the main window
        self.grab_set()          # blocks interaction with the main window until closed

        self._on_save = on_save

        form = tk.Frame(self, padx=15, pady=15)
        form.pack(fill=tk.BOTH, expand=True)

        tk.Label(form, text="Robot IP address:").grid(row=0, column=0, sticky="w", pady=(0, 5))
        self.robot_ip_var = tk.StringVar(value=current_robot_ip)
        tk.Entry(form, textvariable=self.robot_ip_var, width=20).grid(row=0, column=1, pady=(0, 5))

        tk.Label(form, text="Simulator IP address:").grid(row=1, column=0, sticky="w")
        self.sim_ip_var = tk.StringVar(value=current_sim_ip)
        tk.Entry(form, textvariable=self.sim_ip_var, width=20).grid(row=1, column=1)

        button_row = tk.Frame(form)
        button_row.grid(row=2, column=0, columnspan=2, pady=(15, 0), sticky="e")
        tk.Button(button_row, text="Cancel", command=self.destroy).pack(side=tk.RIGHT, padx=(5, 0))
        tk.Button(button_row, text="Save", command=self._save).pack(side=tk.RIGHT)

        # center over the parent window
        self.update_idletasks()
        x = parent.winfo_x() + (parent.winfo_width() - self.winfo_width()) // 2
        y = parent.winfo_y() + (parent.winfo_height() - self.winfo_height()) // 2
        self.geometry(f"+{x}+{y}")

    def _save(self):
        robot_ip = self.robot_ip_var.get().strip()
        sim_ip = self.sim_ip_var.get().strip()

        for label, value in (("Robot IP", robot_ip), ("Simulator IP", sim_ip)):
            try:
                ipaddress.ip_address(value)
            except ValueError:
                messagebox.showerror("Invalid IP", f"{label} '{value}' doesn't look like a valid IP address.")
                return

        self._on_save(robot_ip, sim_ip)
        self.destroy()