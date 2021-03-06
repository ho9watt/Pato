package com.example.pato;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TosBoardActivity extends AppCompatActivity {

    private LinearLayout service,privacy,about,emailSend,linearLayout_1;

    private String privacy_tos = "'pato' 개인정보 처리방침\n" +
            "\n" +
            "'pato'는 개인정보 보호법 제30조에 따라 정보주체의 개인정보를 보호하고 이와 관련한 고충을 신속하고 원활하게 처리할 수 있도록 하기 위하여 다음과 같이 개인정보 처리지침을 수립 공개합니다. \n" +
            "\n" +
            "개인정보의 처리목적 : 'pato'는 다음의 목적을 위하여 개인정보를 처리합니다. 처리하고 있는 개인정보는 다음의 목적 이외의 용도로는 이용되지 않으며, 이용 목적이 변경되는 경우에는 개인정보 보호법 제18조에 따라 별도의 동의를 받는 등 필요한 조치를 이행할 예정입니다. \n" +
            "\n" +
            "1. 안드로이드 앱 회원 가입 및 관리 \n" +
            "\n" +
            "2. 콘텐츠 제공\n" +
            "\n" +
            "\n" +
            "개인정보의 처리 및 보유기간 : 'pato'는 법령에 따른 개인정보 보유 이용기간 또는 정보주체로부터 개인정보를 수집시에 동의받은 개인정보 보유 이용기간 내에서 개인정보를 처리 보유합니다. \n" +
            "\n" +
            "1. 안드로이드 앱 가입 및 관리 : 안드로이드 앱 탈퇴시까지 \n" +
            "\n" +
            "\n" +
            "정보주체와 법정대리인의 권리 의무 및 행사방법\n" +
            "\n" +
            "1. 정보주체는 'pato'에 대해 언제든지 개인정보 조회, 수정, 정정 요구, 삭제의 권리를 행사할 수 있습니다.\n" +
            "\n" +
            "\n" +
            "처리하는 개인정보 항목 :  'pato'는 다음의 개인정보 항목을 처리하고 있습니다. \n" +
            "\n" +
            "1. 안드로이드 앱 회원 가입 및 관리(이메일, 로그인 ID, 닉네임, 비밀번호)\n" +
            "\n" +
            "2. 인터넷 서비스 이용과정에서 아래 개인정보 항목이 자동으로 생성되어 기록 및 수집될 수 있습니다.(안드로이드 스마트폰 고유번호(IMEI), 서비스 이용 기록, 접속 IP정보)\n" +
            "\n" +
            "\n" +
            "개인정보의 파기\n" +
            "\n" +
            "1. 'pato'는 개인정보 보유기간의 경과, 처리목적 달성 등 개인정보가 불필요하게 되었을 때에는 지체없이 해당 개인정보를 파기합니다. \n" +
            "\n" +
            "2. 정보주체로부터 동의받은 개인정보 보유기간이 경과하거나 처리목적이 달성되었음에도 불구하고 다른 법령에 따라 개인정보를 계속 보존하여야 하는 경우에는, 해당 개인정보를 별도의 데이터베이스(DB)로 옮기거나 보관장소를 달리하여 보존합니다. \n" +
            "\n" +
            "3.  개인정보 파기의 방법은 다음과 같습니다. ('pato' 는 전자적 파일 형태로 기록 저장된 개인정보는 기록을 재생할 수 없도록 파기하며, 종이 문서에 기록 저장된 개인정보는 분쇄기로 분쇄하거나 소각하여 파기합니다.)\n" +
            "\n" +
            "\n" +
            "개인정보의 안전성 확보조치 :  'pato'은(는) 개인정보의 안전성 확보를 위해 다음과 같은 조치를 취하고 있습니다. \n" +
            "\n" +
            "1. 비밀번호의 암호화\n" +
            "\n" +
            "이용자의 비밀번호는 암호화 되어 저장 및 관리되고 있어, 본인만이 알 수 있으며 개인정보의 확인, 변경 및 탈퇴(파기)도 비밀번호를 알고 있는 본인에 의해서만 가능합니다.\n" +
            "\n" +
            "\n" +
            "개인정보 자동 수집 장치의 설치 운영 및 거부에 관한 사항\n" +
            "\n" +
            "- Google 애널리틱스를 통한 앱의 정보를 수집중이며 이용자 개인을 식별할 수 없습니다.\n" +
            "\n" +
            "\n" +
            "개인정보보호책임자 및 담당자의 연락처\n" +
            "\n" +
            "1. E-mail: dbdpsdbdk@naver.com\n" +
            "2. 이름: 김정훈\n" +
            "\n" +
            "기타 개인정보침해에 대한 신고나 상담이 필요하신 경우에는 아래 기관에 문의하시기 바랍니다.\n" +
            "\n" +
            "개인정보 분쟁조정위원회 (www.kopico.go.kr, 전화 1833-6972)\n" +
            "개인정보침해신고센터 (privacy.kisa.or.kr / 국번없이 118)\n" +
            "대검찰청 사이버범죄수사단 (www.spo.go.kr / 02-3480-3571)\n" +
            "경찰청 사이버안전국 (cyberbureau.police.go.kr / 국번없이 182)\n" +
            "청소년정보이용안전망 그린 i-Net (www.greeninet.or.kr / 02-523-3566)\n" +
            "\n" +
            "\n" +
            "개인정보 처리방침 변경\n" +
            "본 개인정보처리방침은 볍령 정책 또는 보안기술의 변경에 따라 내용의 추가, 삭제 및 수정이 있을 시에는 변경이 되는 개인정보 처리방침을 시행하기 최소 7일전에 홈페이지의 '공지사항'을 통해 고지할 것입니다.\n" +
            "\n" +
            "개인정보처리방침 시행일자 : 2018년 12월 01일\n" +
            "개인정보처리방침 변경공고일자 : 2018년 12월 01일\n" +
            "\n";

    private String service_tos = "회원가입약관\n" +
            "\n" +
            "제1조(목적)\n" +
            "이 약관은 \"pato\"에서 제공하는 모바일 관련 서비스를 이용함에 있어 이용자의 권리·의무 및 책임사항을 규정함을 목적으로 합니다.\n" +
            " \n" +
            "제2조(정의)\n" +
            "① \"이용자\"란 \"pato\"에 접속하여 이 약관에 따라 \"pato\"이 제공하는 서비스를 받는 회원 및 비회원을 말합니다.\n" +
            "② \"회원\"이라 함은 \"pato\"에 개인정보를 제공하여 회원등록을 한 자로서, \"pato\"의 정보를 지속적으로 제공받으며, \"pato\"가 제공하는 서비스를 계속적으로 이용할 수 있는 자를 말합니다.\n" +
            "③ \"비회원\"이라 함은 회원에 가입하지 않고 \"pato\"가 제공하는 서비스를 이용하는 자를 말합니다.\n" +
            " \n" +
            "제3조 (약관의 명시와 개정)\n" +
            "① \"pato\"가 약관을 개정할 경우에는 적용일자 및 개정사유를 명시하여 현행약관과 함께 pato의 초기화면에 그 적용일자 7일 이전부터 적용일자 전일까지 공지합니다.\n" +
            "② 이 약관에서 정하지 아니한 사항과 이 약관의 해석에 관하여는 정부가 제정한 관계법령 또는 상관례에 따릅니다.\n" +
            " \n" +
            "제4조(서비스의 제공 및 변경)\n" +
            "① \"pato\"는 다음과 같은 업무를 수행합니다.\n" +
            "1. 회원이 게시글, 댓글 작성가능한 공간제공\n" +
            "2. 기타 \"pato\"이 정하는 업무\n" +
            " \n" +
            "제5조(서비스의 중단)\n" +
            "① \"pato\"는 컴퓨터 등 정보통신설비의 보수점검·교체 및 고장, 통신의 두절 등의 사유가 발생한 경우에는 서비스의 제공을 일시적으로 중단할 수 있습니다.\n" +
            "② 제1항에 의한 서비스 중단의 경우에는 \"pato\"는 제8조에 정한 방법으로 이용자에게 통지합니다.\n" +
            " \n" +
            "제6조(회원가입)\n" +
            "① 이용자는 \"pato\"가 정한 가입 양식에 따라 회원정보를 기입한 후 이 약관에 동의한다는 의사표시를 함으로서 회원가입을 신청합니다.\n" +
            "② \"pato\"는 제1항과 같이 회원으로 가입할 것을 신청한 이용자 중 다음 각호에 해당하지 않는 한 회원으로 등록합니다.\n" +
            "1. 가입신청자가 이 약관 제7조 제3항에 의하여 이전에 회원자격을 상실한 적이 있는 경우. 다만, 제7조 제3항에 의한 회원자격 상실 후 3년이 경과한 자로서 \"pato\"의 회원재가입 승낙을 얻은 경우에는 예외로 한다.\n" +
            "2. 등록 내용에 허위, 기재누락, 오기가 있는 경우\n" +
            "3. 기타 회원으로 등록하는 것이 \"pato\"의 기술상 현저히 지장이 있다고 판단되는 경우\n" +
            "③ 회원가입의 성립시기는 \"pato\"의 승낙이 회원에게 도달한 시점으로 합니다.\n" +
            "④ 회원은 제9조 제1항에 의한 등록사항에 변경이 있는 경우, 즉시 전자우편이나 기타 방법으로 \"pato\"에 대하여 그 변경사항을 알려야 합니다.\n" +
            " \n" +
            "제7조(회원 탈퇴 및 자격 상실 등)\n" +
            "① 회원은 \"pato\"에서 언제든지 탈퇴를 요청할 수 있으며 \"pato\"는 탈퇴요청한 회원의 개인정보 보호기간이 끝남과 동시에 파기합니다\n" +
            "② 회원이 다음 각호의 사유에 해당하는 경우, \"pato\"는 회원자격을 제한 및 정지시킬 수 있습니다.\n" +
            "1. 가입 신청시에 허위 내용을 등록한 경우\n" +
            "2. 다른 사람의 \"pato\" 이용을 방해하거나 그 정보를 도용하는 등 게시판정서를 위협하는 경우\n" +
            "3. \"pato\"를 이용하여 법령과 이 약관이 금지하거나 공지사항에 반하는 행위를 하는 경우\n" +
            "③ \"pato\"는 회원 자격을 제한·정지 시킨후, 동일한 행위가 2회 이상 반복되거나 30일 이내에 그 사유가 시정되지 아니하는 경우 \"pato\"은 회원자격을 상실시킬 수 있습니다.\n" +
            "④ \"pato\"가 회원자격을 상실시키는 경우에는 회원등록을 말소합니다. \n" +
            " \n" +
            "제8조(회원에 대한 통지)\n" +
            "① \"pato\"이 회원에 대한 통지를 하는 경우, 회원이 \"pato\"에 제출한 전자우편 주소로 할 수 있습니다.\n" +
            "② \"pato\"은 불특정다수 회원에 대한 통지의 경우 \"pato\" 공지사항 게시판에 게시함으로서 개별 통지에 갈음할 수 있습니다.\n" +
            " \n" +
            "제9조(개인정보보호)\n" +
            "① \"pato\"는 이용자의 정보수집시 서비스 제공에 필요한 최소한의 정보를 수집합니다.\n" +
            "- 개인정보보호 항목은 개인정보취급방침에서 자세하게 다루고 있습니다.\n" +
            " \n" +
            "제10조(\"pato\"의 의무)\n" +
            "① \"pato\"는 법령과 이 약관이 금지하거나 공서양속에 반하는 행위를 하지 않으며 이 약관이 정하는 바에 따라 지속적이고, 안정적으로 서비스를 제공하는 데 최선을 다하여야 합니다.\n" +
            "② \"pato\"는 이용자가 안전하게 인터넷 서비스를 이용할 수 있도록 이용자의 개인정보(신용정보 포함)보호를 위한 보안 시스템을 갖추어야 합니다.\n" +
            "③ \"pato\"는 이용자가 원하지 않는 영리목적의 광고성 전자우편을 발송하지 않습니다.\n" +
            " \n" +
            "제11조(회원의 ID 및 비밀번호에 대한 의무)\n" +
            "① 제9조의 경우를 제외한 ID와 비밀번호에 관한 관리책임은 회원에게 있습니다.\n" +
            "② 회원은 자신의 ID 및 비밀번호를 제3자에게 이용하게 해서는 안됩니다.\n" +
            "③ 회원이 자신의 ID 및 비밀번호를 도난당하거나 제3자가 사용하고 있음을 인지한 경우에는 바로 \"pato\"에 통보하고 \"pato\"의 안내가 있는 경우에는 그에 따라야 합니다.\n" +
            " \n" +
            "제12조(이용자의 의무)\n" +
            "이용자는 다음 행위를 하여서는 안됩니다.\n" +
            "1. 회원가입 신청 또는 변경시 허위내용 등록\n" +
            "2. \"pato\"에 게시된 정보의 변경\n" +
            "3. \"pato\"가 정한 정보 이외의 정보(컴퓨터 프로그램 등)의 송신 또는 게시\n" +
            "4. \"pato\" 기타 제3자의 저작권 등 지적재산권에 대한 침해\n" +
            "5. \"pato\" 기타 제3자의 명예를 손상시키거나 업무를 방해하는 행위\n" +
            "6. 외설 또는 폭력적인 메시지·화상·음성 기타 공서양속에 반하는 정보를 pato에 공개 또는 게시하는 행위\n" +
            "7. pato 공지사항에서 제한하는 행위\n" +
            " \n" +
            "제13조(저작권의 귀속 및 이용제한)\n" +
            "① 회원이 작성한 저작물에 대한 저작권 기타 지적재산권은 \"pato\"에 귀속합니다.\n" +
            "② 이용자는 \"pato\"을 이용함으로써 얻은 정보를 \"pato\"의 사전 승낙없이 복제, 송신, 출판, 배포, 방송 기타 방법에 의하여 영리목적으로 이용하거나 제3자에게 이용하게 하여서는 안됩니다.\n" +
            " \n" +
            "제14조(분쟁해결)\n" +
            "① \"pato\"은 이용자가 제기하는 정당한 의견이나 불만을 반영하고 그 의견이나 불만을 처리할 게시판을 운영합니다\n" +
            "② \"pato\"은 이용자로부터 제출되는 불만사항 및 의견은 우선적으로 그 사항을 처리합니다. 다만, 신속한 처리가 곤란한 경우에는 이용자에게 그 사유와 처리일정을 즉시 통보해 드립니다.";

    private String about_pato = "\n\n8 W A T T\n\n*\n\n*\n\n*\n\n*\n\n*\n\n*";

    private ScrollView scrollView;

    private TextView intro ;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private Button removeUser, emailSend_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tos_board);

        service =  findViewById(R.id.tosBoard_serviceTos);
        privacy =  findViewById(R.id.tosBoard_privacyTos);
        about =  findViewById(R.id.tosBoard_aboutPato);
        emailSend = findViewById(R.id.tosBoard_email_verified);
        scrollView =  findViewById(R.id.myFragment_scroll_view);
        intro =  findViewById(R.id.tosBoard_Activity_tView);
        removeUser = findViewById(R.id.tosBoard_removeUser);
        emailSend_Btn = findViewById(R.id.tosBoard_email_send);
        linearLayout_1 = findViewById(R.id.tosBoard_linear_1);

        intro.setText(service_tos);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            removeUser.setVisibility(View.VISIBLE);
            if(!firebaseUser.isEmailVerified()){
                emailSend_Btn.setVisibility(View.VISIBLE);
            }else{
                emailSend_Btn.setVisibility(View.GONE);
            }
        }else{
            linearLayout_1.setVisibility(View.GONE);
            emailSend.setVisibility(View.GONE);
        }
        clickListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 4){
            if(resultCode== Activity.RESULT_OK){
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK,intent);
                TosBoardActivity.this.finish();

            }else{

            }
        }
    }

    private void clickListener(){
        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intro.setText(service_tos);
                scrollView.scrollTo(0,0);
                intro.setGravity(Gravity.NO_GRAVITY);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intro.setText(privacy_tos);
                scrollView.scrollTo(0,0);
                intro.setGravity(Gravity.NO_GRAVITY);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intro.setText(about_pato);
                scrollView.scrollTo(0,0);
                intro.setGravity(Gravity.CENTER);
            }
        });
        emailSend_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            TosBoardActivity.this.finish();
                            Toast.makeText(TosBoardActivity.this, "이메일을 전송하였습니다.", Toast.LENGTH_SHORT).show();
                            emailSend_Btn.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(TosBoardActivity.this, "이메일을 전송할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        removeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RemoveUserActivity.class);
                startActivityForResult(intent,4);
            }
        });

        getSupportActionBar().setTitle("ABOUT PATO");
    }

}
