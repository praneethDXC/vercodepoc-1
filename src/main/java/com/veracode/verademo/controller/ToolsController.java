package com.veracode.verademo.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Scope("request")
public class ToolsController {
    private static final Logger logger = LogManager.getLogger("VeraDemo:ToolsController");

    @Autowired
    ServletContext context;

    @RequestMapping(value = "/tools", method = RequestMethod.GET)
    public String tools() {
        return "tools";
    }

    @RequestMapping(value = "/tools", method = RequestMethod.POST)
    public String tools(@RequestParam(value = "host", required = false) String host, @RequestParam(value = "fortunefile", required = false) String fortuneFile, Model model) {
        model.addAttribute("ping", host != null ? ping(host) : "");

        if (fortuneFile == null) {
            fortuneFile = "literature";
        }
        model.addAttribute("fortunes", fortune(fortuneFile));

        return "tools";
    }

    private String ping(String host) {
        String output = "";
        Process proc;

        logger.info("Pinging: " + host);

        try {
            if (!isValidHost(host)) {
                throw new IllegalArgumentException("Invalid host provided.");
            }

            proc = Runtime.getRuntime().exec(new String[] { "ping", "-c1", host });

            proc.waitFor(5, TimeUnit.SECONDS);
            InputStreamReader isr = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            String line;

            while ((line = br.readLine()) != null) {
                output += line + "\n";
            }

            logger.info(proc.exitValue());
        } catch (IOException ex) {
            logger.error(ex);
        } catch (InterruptedException ex) {
            logger.error(ex);
        }

        return output;
    }

    private String fortune(String fortuneFile) {
        String output = "";
        Process proc;

        try {
            if (!isValidFortuneFile(fortuneFile)) {
                throw new IllegalArgumentException("Invalid fortune file provided.");
            }

            proc = Runtime.getRuntime().exec(new String[] { "/bin/fortune", fortuneFile });

            proc.waitFor(5, TimeUnit.SECONDS);
            InputStreamReader isr = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            String line;

            while ((line = br.readLine()) != null) {
                output += line + "\n";
            }
        } catch (IOException ex) {
            logger.error(ex);
        } catch (InterruptedException ex) {
            logger.error(ex);
        }

        return output;
    }

    private boolean isValidHost(String host) {
        try {
            InetAddress.getByName(host);
            return host.matches("^[a-zA-Z0-9.-]+$");
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private boolean isValidFortuneFile(String fortuneFile) {
        return fortuneFile.matches("^[a-zA-Z0-9._/-]+$");
    }
}