package io.ont.orangeapexample.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.Helper;
import io.ont.entity.OrangeProviderOntSdk;
import io.ont.orangeapexample.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@Slf4j
public class APController {

    @GetMapping("/ping")
    public DefaultResp checkMessage() {
        DefaultResp pong = DefaultResp.builder().message("pong").build();
        return pong;
    }


    String selfDID = "did:ont:Af3r35XWVCmnXRfAkGEs2vSGfeZhS24NoT";

    @PostMapping("/calcScore")
    public Result DPRequest1(@RequestBody BalanceReq dataWithSig) {
        OrangeProviderOntSdk orangeProviderOntSdk = null;
        try {
            orangeProviderOntSdk = OrangeProviderOntSdk.getOrangeProviderOntSdk("testnet", "./wallet.dat", "passwordtest");
        } catch (Exception e) {
            log.info("generate orangeSdk error ");
            e.printStackTrace();
        }

        String encrypted = dataWithSig.getEncrypted();
        byte[] decryptedbts = orangeProviderOntSdk.decryptData(Helper.hexToBytes(encrypted));
        JSONObject jsonObject = JSON.parseObject(new String(decryptedbts));
        String sig = jsonObject.get("sig").toString();
        System.out.println("sig data : " + sig);
        JSONObject data = (JSONObject) jsonObject.get("data");
        String balance = data.get("balance").toString();

        BalanceData balanceDataVertify = new BalanceData();
        balanceDataVertify.setBalance("1000000");

        byte[] msgbytes = JSON.toJSONString(balanceDataVertify).getBytes();
        byte[] sigbytes = Helper.hexToBytes(sig);
        Boolean isVerfy = orangeProviderOntSdk.verifySig(orangeProviderOntSdk.getSelfDID(), msgbytes, sigbytes);
        System.out.println(" Verify Signature result is " + isVerfy);
        System.out.println("balance is " + balance);
        return Result.builder().score(500).build();
    }

}
