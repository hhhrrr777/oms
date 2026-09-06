<template>
  <div class="login">
    <!-- 全页背景装饰层 -->
    <div class="deco">
      <span class="orb orb-1"></span>
      <span class="orb orb-2"></span>
      <span class="orb orb-3"></span>
      <span class="ring ring-1"></span>
      <span class="ring ring-2"></span>
      <div class="beam"></div>
    </div>

    <!-- 左侧品牌视觉区 -->
    <div class="login-left">
      <div class="brand-logo">
        <span class="logo-badge">W</span>
        <span class="logo-name">WayF</span>
      </div>

      <div class="brand-hero">
        <h1>一站式电商<br />订单运营管理后台</h1>
        <p>订单 · 商品 · 库存 · 售后 · 财务，全链路数字化管理，助力业务高效增长。</p>
      </div>

      <div class="brand-foot">Copyright © 2023-2026 WayF All Rights Reserved.</div>
    </div>

    <!-- 右侧表单区 -->
    <div class="login-right">
      <div class="login-form">
        <div class="form-brand">
          <span class="fb-badge">W</span>
          <div class="fb-text">
            <div class="fb-name">WayF</div>
            <div class="fb-slogan">电商订单运营中台</div>
          </div>
        </div>

        <h2 class="form-title">欢迎登录 👋</h2>
        <div class="form-sub">请使用您的账号登录后台管理系统</div>

        <el-form ref="loginForm" :model="loginForm" :rules="loginRules">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="账号/手机号"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="密码"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input
              v-model="loginForm.code"
              auto-complete="off"
              placeholder="验证码"
              style="width: 63%"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
            </el-input>
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img"/>
            </div>
          </el-form-item>

          <div class="form-row">
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
            <span class="forgot" @click="forgotPwd">忘记密码？</span>
          </div>

          <el-form-item style="width:100%;">
            <el-button class="btns"
                       :loading="loading"
                       size="medium"
                       type="primary"
                       style="width:100%;"
                       @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <div class="right-foot">
          <span @click="comingSoon">帮助中心</span>
          <i></i>
          <span @click="comingSoon">隐私政策</span>
          <i></i>
          <span @click="comingSoon">服务条款</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'
import { getCodeImg } from "@/api/login";
import {getConfig} from "@/api/system/config";
import {validatePassword} from "@/utils/validate";
export default {
  name: "Login",
  data() {
    return {
      n:1,
      codeUrl: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      title:'',
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      passwordError: null,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined,

      show: true,
      isCode: false,
      count: 60,
      form: {
        phone: "",
        smsCode: "",
        checkMove: "",
      },
      codeRules: {
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
        ],
        smsCode: [{ required: true, message: '请输入手机验证码', trigger: 'blur' }],
        checkMove: [{ required: true, message: '滑动完成验证', trigger: 'blur' }],
      },

      registerForm:{
        phone: "",
        passwords: "",
        passwordok: "",
      },
      registerRules: {
        phone: [
          { required: true, message: '请输入您的账号', trigger: 'blur' },
        ],
        passwords: [{ required: true, message: '请输入您的密码', trigger: 'blur' }],
        passwordok: [{ required: true, message: '请再此确认您的密码', trigger: 'blur' }],
      },
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.loading =true
    getConfig('sys.name').then(resp=>{
      if(resp.data){
        this.title = resp.data.configValue
        this.getCode();
        this.getCookie();
      }
    })

  },
  // directives: {
  //   move(el, binding, vnode) {
  //     let vm = this;
  //     el.onmousedown = function (e) {
  //       var X = e.clientX - el.offsetLeft;
  //       document.onmousemove = function (e) {
  //         var endx = e.clientX - X;
  //         el.className = "move moveBefore";
  //         el.style.left = endx + "px";
  //         var width = document.querySelector(".movebox").offsetWidth - document.querySelector(".move").offsetWidth;
  //         el.parentNode.children[0].style.width = endx + 20 + "px";
  //         el.parentNode.children[1].innerHTML = "按住滑块,拖动到最右边";
  //         //临界值小于
  //         if (endx <= 0) {
  //           el.style.left = 0 + "px";
  //           el.parentNode.children[0].style.width = 0 + "px";
  //         }
  //
  //         //临界值大于
  //         if (parseInt(el.style.left) >= width) {
  //           if(vnode.context.form.phone == ''){
  //             el.style.left = 0 + "px";
  //             el.parentNode.children[0].style.width = 0 + "px";
  //             el.className = "move moveBefore";
  //             document.onmousemove = null;
  //             vnode.context.$message({
  //               message: '请输入手机号',
  //               type: 'warning'
  //             });
  //           }else{
  //             el.style.left = width + "px";
  //             el.parentNode.children[0].style.width = width + 20 + "px";
  //             el.parentNode.children[1].innerHTML = "<span style='-webkit-text-fill-color: #fff;'>验证通过</span>";
  //             el.className = "move moveSuccess";
  //             document.onmousemove = null;
  //             el.onmousedown = null;
  //             binding.value()
  //           }
  //         }
  //       };
  //     };
  //     document.onmouseup = function () {
  //       document.onmousemove = null;
  //     };
  //   }
  // },
  methods: {
    comingSoon() {
      this.$message("该功能暂未开放");
    },
    forgotPwd() {
      this.$message("请联系系统管理员重置密码");
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      const vm = this
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;

          // 验证密码强度
          // if (!vm.validatePassword()) {
          //   vm.$modal.msgError(vm.passwordError)
          //   this.loading = false
          //   return
          // }
          // const res = validatePassword(this.loginForm.password,this.loginForm.username)
          // if (!res.result) {
          //   this.$modal.msgError(res.msg)
          //   this.loading = false
          //   return
          // }

          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
          }).catch(() => {
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
          });
        }
      });
    },
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
        this.loading = false
      });
    },

    // 密码验证方法
    validatePassword() {
      this.passwordError = ""; // 清除之前的错误信息
      // 1. 检查密码长度
      if (this.loginForm.password.length < 8 || this.loginForm.password.length > 32) {
        this.passwordError = "密码长度必须在 8 到 32 个字符之间";
        return false;
      }

      // 2. 检查密码是否包含大小写字母、数字和特殊字符
      const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*(),.?":{}|<>]).+$/;
      if (!regex.test(this.loginForm.password)) {
        this.passwordError = "密码必须包含大小写字母、数字和特殊字符";
        return false;
      }

      // 3. 禁止连续或重复字符
      if (this.hasSequentialOrRepeatedChars(this.loginForm.password)) {
        this.passwordError = "密码不能包含连续或重复字符";
        return false;
      }

      // 4. 密码不能与用户名相同
      if (this.loginForm.password.toLowerCase() === this.loginForm.username.toLowerCase()) {
        this.passwordError = "密码不能与用户名相同";
        return false;
      }

      // 密码符合要求
      return true;
    },

    // 检查密码是否包含连续或重复字符
    hasSequentialOrRepeatedChars(password) {
      // 检查连续数字或字母
      for (let i = 0; i < password.length - 2; i++) {
        const current = password.charAt(i);
        const next = password.charAt(i + 1);
        const nextNext = password.charAt(i + 2);
        if (next === current + 1 && nextNext === current + 2) {
          return true;
        }
      }

      // 检查重复字符
      for (let i = 0; i < password.length - 1; i++) {
        if (password.charAt(i) === password.charAt(i + 1)) {
          return true;
        }
      }

      return false;
    }

  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  width: 100%;
  height: 100%;
  overflow: hidden;
  position: relative;
  font-family: "Helvetica Neue", Arial, "PingFang SC", "Microsoft YaHei", sans-serif;
  background: linear-gradient(150deg, #16235e 0%, #1d39c4 32%, #307dff 68%, #5aa9ff 100%);
}
/* 细网点纹理 */
.login::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: radial-gradient(rgba(255, 255, 255, .16) 1px, transparent 1px);
  background-size: 24px 24px;
  opacity: .4;
  z-index: 1;
}

/* ---- 背景装饰层 ---- */
.deco { position: absolute; inset: 0; z-index: 0; }
.orb { position: absolute; border-radius: 50%; filter: blur(70px); }
.orb-1 { width: 420px; height: 420px; left: -120px; top: -120px; background: rgba(105, 177, 255, .55); }
.orb-2 { width: 380px; height: 380px; right: -100px; bottom: -80px; background: rgba(123, 92, 255, .45); }
.orb-3 { width: 260px; height: 260px; left: 30%; bottom: 6%; background: rgba(22, 194, 163, .35); }
.ring {
  position: absolute; border-radius: 50%;
  border: 1.5px solid rgba(255, 255, 255, .18);
}
.ring-1 { width: 300px; height: 300px; right: -70px; top: 8%; }
.ring-2 { width: 200px; height: 200px; right: -20px; top: 12%; border-color: rgba(255,255,255,.12); }
/* 斜向光束 */
.beam {
  position: absolute; top: -30%; left: 38%;
  width: 220px; height: 170%;
  background: linear-gradient(180deg, rgba(255,255,255,.14), rgba(255,255,255,0));
  transform: rotate(18deg);
  filter: blur(6px);
}

/* ================= 左侧品牌视觉区（透明，浮于背景上） ================= */
.login-left {
  flex: 1.1;
  position: relative;
  z-index: 3;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 48px 56px;
  color: #fff;
}

.brand-logo {
  display: flex; align-items: center; gap: 10px;
  font-size: 20px; font-weight: 600;
  position: relative; z-index: 3;
}
.logo-badge {
  width: 36px; height: 36px; border-radius: 10px;
  background: rgba(255, 255, 255, .18);
  border: 1px solid rgba(255, 255, 255, .25);
  display: flex; align-items: center; justify-content: center;
  font-weight: 800; font-size: 18px;
  backdrop-filter: blur(4px);
}
.brand-hero { position: relative; z-index: 3; max-width: 460px; }
.brand-hero h1 {
  font-size: 36px; line-height: 1.35; font-weight: 700;
  margin-bottom: 18px; letter-spacing: 1px;
  text-shadow: 0 2px 20px rgba(10, 30, 90, .35);
}
.brand-hero p { font-size: 15px; opacity: .85; line-height: 1.9; }
.brand-foot { position: relative; z-index: 3; font-size: 12px; opacity: .7; letter-spacing: .5px; }

/* ================= 右侧表单区 ================= */
.login-right {
  flex: 1;
  position: relative;
  z-index: 3;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 24px;
}
.login-form {
  width: 380px;
  background: #fff;
  border-radius: 18px;
  padding: 40px 38px 28px;
  box-shadow: 0 32px 80px rgba(8, 22, 70, .35);
  border: 1px solid rgba(255, 255, 255, .5);
}

/* 品牌头 */
.form-brand { display: flex; align-items: center; gap: 12px; margin-bottom: 26px; }
.fb-badge {
  width: 42px; height: 42px; border-radius: 12px;
  background: linear-gradient(135deg, #307dff, #5aa9ff);
  color: #fff; font-weight: 800; font-size: 20px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 8px 18px rgba(48, 125, 255, .35);
}
.fb-name { font-size: 18px; font-weight: 700; color: #1f2329; line-height: 1.2; }
.fb-slogan { font-size: 12px; color: #9aa0aa; }

.form-title { font-size: 25px; font-weight: 700; color: #1f2329; margin-bottom: 8px; }
.form-sub { color: #8a8f99; font-size: 14px; margin-bottom: 28px; }

.login-form .el-form-item { margin-bottom: 20px; }
.login-form .el-input { height: 46px; }
.login-form .el-input input {
  height: 46px; line-height: 46px;
  border-color: #e4e7ed; border-radius: 8px;
  padding-left: 40px; color: #1f2329;
  transition: border-color .2s, box-shadow .2s;
}
.login-form .el-input input:focus {
  border-color: #307dff;
  box-shadow: 0 0 0 3px rgba(48, 125, 255, .12);
}
.input-icon { height: 46px; width: 16px; margin-left: 12px; color: #9aa0aa; }

/* 记住密码 / 忘记密码 */
.form-row {
  display: flex; align-items: center; justify-content: space-between;
  margin: -4px 0 18px;
}
.forgot { font-size: 13px; color: #307dff; cursor: pointer; }
.forgot:hover { text-decoration: underline; }

.btns {
  width: 100%; height: 46px; padding: 0;
  color: #fff; font-size: 15px; font-weight: 500; letter-spacing: 6px;
  background: linear-gradient(90deg, #307dff, #4a9bff);
  border: none; border-radius: 8px;
  box-shadow: 0 8px 18px rgba(48, 125, 255, .3);
}
.btns:hover, .btns:focus { background: linear-gradient(90deg, #2f7bff, #5aa9ff); color: #fff; }

/* 右下底部链接 */
.right-foot {
  margin-top: 28px; display: flex; align-items: center; justify-content: center;
  gap: 12px; font-size: 12px; color: #b6bcc7;
}
.right-foot span { cursor: pointer; }
.right-foot span:hover { color: #307dff; }
.right-foot i { width: 3px; height: 3px; border-radius: 50%; background: #d5dae2; }

.login-code { width: 33%; height: 46px; float: right; }
.login-code-img {
  height: 46px; width: 100%; object-fit: cover;
  border-radius: 8px; cursor: pointer; vertical-align: middle;
}

/* 窄屏收起左栏，只留表单 */
@media (max-width: 900px) {
  .login-left { display: none; }
  .login-form { box-shadow: 0 24px 60px rgba(8, 24, 80, .3); border: none; padding: 32px 24px; }
}
</style>
