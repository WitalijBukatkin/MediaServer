### Configuration linux system

#### Preliminary
```console

apt-get update
apt update -y
apt upgrade -y

echo mediaserver > /etc/hostname

dpkg-reconfigure tzdata #Time conf

echo "rock64 ALL=NOPASSWD: ALL" >> /etc/sudoers  #Disable password root
```

### Enable root in SSH
```console
sudo passwd
sudo echo PermitRootLogin yes >> /etc/ssh/sshd_config
sudo service ssh reload
```

### Create ssh key
```console
ssh-keygen
ssh-copy-id root@192.168.1.34
ssh-add
```

### Update kernel for fan
All: https://github.com/ayufan-rock64/linux-kernel/releases/tag/4.4.132-1077-rockchip-ayufan

```console
wget https://github.com/ayufan-rock64/linux-kernel/releases/download/4.4.132-1077-rockchip-ayufan/linux-firmware-image-4.4.132-1077-rockchip-ayufan-gbaf35a9343cb_4.4.132-1077-rockchip-ayufan_arm64.deb
wget https://github.com/ayufan-rock64/linux-kernel/releases/download/4.4.132-1077-rockchip-ayufan/linux-headers-4.4.132-1077-rockchip-ayufan-gbaf35a9343cb_4.4.132-1077-rockchip-ayufan_arm64.deb
wget https://github.com/ayufan-rock64/linux-kernel/releases/download/4.4.132-1077-rockchip-ayufan/linux-image-4.4.132-1077-rockchip-ayufan-gbaf35a9343cb-dbg_4.4.132-1077-rockchip-ayufan_arm64.deb
wget https://github.com/ayufan-rock64/linux-kernel/releases/download/4.4.132-1077-rockchip-ayufan/linux-image-4.4.132-1077-rockchip-ayufan-gbaf35a9343cb_4.4.132-1077-rockchip-ayufan_arm64.deb
wget https://github.com/ayufan-rock64/linux-kernel/releases/download/4.4.132-1077-rockchip-ayufan/linux-libc-dev_4.4.132-1077-rockchip-ayufan_arm64.deb
dpkg -i *.deb
rm -r *.deb
apt remove -y *4.4.132-1075*
apt autoremove -y
```

### Install Wifi drivers rtl8812au
```console
git clone https://github.com/gordboy/rtl8812au
ln -s /lib/modules/`uname -r`/build/arch/arm64 /lib/modules/`uname -r`/build/arch/aarch64
apt install -y python dkms
cp -r rtl8812au /usr/src/rtl8812au-5.2.20
cd /usr/src/rtl8812au-5.2.20
make
make install
dkms add -m rtl8812au -v 5.2.20
dkms build -m rtl8812au -v 5.2.20
dkms install -m rtl8812au -v 5.2.20
cd
rm -r rtl8812au
```

### Configure hotspot
```console
apt install -y iptables hostapd dnsmasq
echo net.ipv4.ip_forward=1 >> /etc/sysctl.conf
echo DAEMON_CONF='/etc/hostapd/hostapd.conf' > /etc/default/hostapd

cat << EOF > /etc/hostapd/hostapd.conf
interface=wlx08107b5413d4
driver=nl80211
ssid=Media Server

country_code=RU
hw_mode=a
ieee80211n=1
ieee80211ac=1
ieee80211d=1

channel=44
vht_oper_chwidth=1
vht_oper_centr_freq_seg0_idx=50
ht_capab=[HT40+]

wpa=2
wpa_passphrase=StmpSmLd
wpa_key_mgmt=WPA-PSK
wpa_pairwise=TKIP
rsn_pairwise=CCMP
EOF

cat << EOF > /etc/dnsmasq.conf
bind-interfaces
interface=wlx08107b5413d4
dhcp-range=192.168.2.2,192.168.2.10,12h
EOF

cat << EOF > /etc/network/interfaces
#loopback
auto lo
iface lo inet loopback

#eth0
auto eth0
iface eth0 inet manual

#wlx08107b5413d4
auto wlx08107b5413d4
iface wlx08107b5413d4 inet static
address 192.168.2.1

post-up iptables -t nat -A POSTROUTING -o "eth0" -j MASQUERADE
EOF

systemctl disable NetworkManager

systemctl unmask hostapd
systemctl unmask dnsmasq

systemctl enable hostapd
systemctl enable dnsmasq

systemctl start hostapd
systemctl start dnsmasq

```