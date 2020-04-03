from rest_framework import viewsets
from .serializers import MemberSerializer
from .models import Member

class MemberView(viewsets.ModelViewSet):
        queryset = Member.objects.all()
        serializer_class = MemberSerializer

# Create your views here.
